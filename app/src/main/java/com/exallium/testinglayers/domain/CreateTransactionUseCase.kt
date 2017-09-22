package com.exallium.testinglayers.domain

import com.exallium.testinglayers.entities.Account
import com.exallium.testinglayers.entities.Transaction
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class CreateTransactionUseCase(private val accountRepository: AccountRepository,
                               private val transactionRepository: TransactionRepository) {

    // creates and verifies a transaction
    sealed class Response {
        object NonPositiveAmount : Response()
        object UnknownFromAccount : Response()
        object UnknownToAccount : Response()
        object InsufficientFunds : Response()
        object InProgress : Response()
        data class Success(val transaction: Transaction) : Response()
        data class Failure(val throwable: Throwable) : Response()
    }

    private sealed class TransactionState {
        object ToAccountExists : TransactionState()
        object FromAccountDNE : TransactionState()
        object ToAccountDNE : TransactionState()
        object FromAccountHasFunds : TransactionState()
        object FromAccountInsufficientFunds : TransactionState()
        data class ValidTransaction(val transaction: Transaction) : TransactionState()
        data class Success(val transaction: Transaction): TransactionState()
        data class Failure(val throwable: Throwable): TransactionState()
    }

    fun createTransaction(fromAccount: Account, toAccount: Account, amount: Long): Observable<Response> {
        return when {
            amount <= 0 -> Observable.just<Response>(Response.NonPositiveAmount)
            fromAccount.id == Account.Id.UNSAVED -> Observable.just<Response>(Response.UnknownFromAccount)
            toAccount.id == Account.Id.UNSAVED -> Observable.just<Response>(Response.UnknownToAccount)
            else -> buildTransaction(fromAccount.id, toAccount.id, amount)
        }.startWith(Response.InProgress)
    }

    private fun buildTransaction(fromId: Account.Id, toId: Account.Id, amount: Long): Observable<Response> {
        // Check that both accounts exist
        val fromAccount = accountRepository.getAccountById(fromId)
                .map<TransactionState> {
                    if (it.balance >= amount) TransactionState.FromAccountHasFunds
                    else TransactionState.FromAccountInsufficientFunds
                }
                .defaultIfEmpty(TransactionState.FromAccountDNE)

        val toAccount = accountRepository.getAccountById(toId)
                .map<TransactionState> { TransactionState.ToAccountExists }
                .defaultIfEmpty(TransactionState.ToAccountDNE)

        // Validate that fromAccount has an acceptable amount of cash
        return fromAccount.zipWith<TransactionState, TransactionState>(toAccount, BiFunction { f, t ->
            if (f == TransactionState.FromAccountHasFunds && t == TransactionState.ToAccountExists)
                TransactionState.ValidTransaction(Transaction(Transaction.Id.UNSAVED, fromId, toId, amount))
            else if (f != TransactionState.FromAccountHasFunds) f
            else t
        }).toObservable().flatMap {
            if (it is TransactionState.ValidTransaction)
                performTransaction(it.transaction)
            else Observable.just(it)
        }.map {
            when (it) {
                is TransactionState.ToAccountDNE -> Response.UnknownToAccount
                is TransactionState.FromAccountDNE -> Response.UnknownFromAccount
                is TransactionState.FromAccountInsufficientFunds -> Response.InsufficientFunds
                is TransactionState.Success -> Response.Success(it.transaction)
                is TransactionState.Failure -> Response.Failure(it.throwable)
                else -> throw IllegalStateException("Unexpected State: $it")
            }
        }
    }

    private fun performTransaction(transaction: Transaction): Observable<TransactionState> {
        val fromAccount = accountRepository.getAccountById(transaction.fromAccount)
        val toAccount = accountRepository.getAccountById(transaction.toAccount)

        val subBalance = fromAccount.map {
            it.copy(balance = it.balance - transaction.amount)
        }.flatMapSingle {
            accountRepository.updateAccount(it)
        }

        val addBalance = toAccount.map {
            it.copy(balance = it.balance + transaction.amount)
        }.flatMapSingle {
            accountRepository.updateAccount(it)
        }

        return subBalance.zipWith<Account, Unit>(addBalance, BiFunction { _, _ -> }).flatMap {
            transactionRepository.createTransaction(transaction)
        }.map<TransactionState> {
            TransactionState.Success(it)
        }.onErrorReturn {
            TransactionState.Failure(it)
        }.toObservable()

    }
}