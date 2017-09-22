package com.exallium.testinglayers.domain

import com.exallium.testinglayers.entities.Account
import com.exallium.testinglayers.entities.Customer
import com.exallium.testinglayers.entities.Transaction
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class CreateTransactionUseCaseTest {

    val fromAccount = Account(Account.Id("from"), Customer.Id("asdf"), 100)
    val toAccount = Account(Account.Id("to"), Customer.Id("fdsa"), 0)
    val transaction = Transaction(Transaction.Id("asdf"), fromAccount.id, toAccount.id, 100)

    val transactionRepository: TransactionRepository = mock()
    val accountRepository: AccountRepository = mock()

    val testSubject = CreateTransactionUseCase(accountRepository, transactionRepository)

    @Before
    fun setUp() {
        whenever(accountRepository.getAccountById(fromAccount.id)).thenReturn(Maybe.just(fromAccount))
        whenever(accountRepository.getAccountById(toAccount.id)).thenReturn(Maybe.just(toAccount))
        whenever(accountRepository.updateAccount(fromAccount.copy(balance = 0)))
                .thenReturn(Single.just(fromAccount.copy(balance = 0)))
        whenever(accountRepository.updateAccount(toAccount.copy(balance = 100)))
                .thenReturn(Single.just(toAccount.copy(balance = 100)))
        whenever(transactionRepository.createTransaction(transaction.copy(id = Transaction.Id.UNSAVED)))
                .thenReturn(Single.just(transaction))
    }

    @Test
    fun createTransaction_whenAmountIsZero_returnsNonPositiveAmount() {
        // GIVEN
        val amount = 0

        // WHEN
        val result = testSubject.createTransaction(fromAccount, toAccount, 0).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateTransactionUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateTransactionUseCase.Response.NonPositiveAmount }
    }

    @Test
    fun createTransaction_whenFromAccountDNE_returnsUnknownFromAccount() {
        // GIVEN
        whenever(accountRepository.getAccountById(fromAccount.id)).thenReturn(Maybe.empty())

        // WHEN
        val result = testSubject.createTransaction(fromAccount, toAccount, 100).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateTransactionUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateTransactionUseCase.Response.UnknownFromAccount }
    }

    @Test
    fun createTransaction_whenToAccountDNE_returnsUnknownToAccount() {
        // GIVEN
        whenever(accountRepository.getAccountById(toAccount.id)).thenReturn(Maybe.empty())

        // WHEN
        val result = testSubject.createTransaction(fromAccount, toAccount, 100).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateTransactionUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateTransactionUseCase.Response.UnknownToAccount }
    }

    @Test
    fun createTransaction_whenInsufficientFunds_returnsInsufficientFunds() {
        // WHEN
        val result = testSubject.createTransaction(fromAccount, toAccount, 1000).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateTransactionUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateTransactionUseCase.Response.InsufficientFunds }
    }

    @Test
    fun createTransaction_whenTransactionSucceeds_returnsSuccessResponse() {
        // WHEN
        val result = testSubject.createTransaction(fromAccount, toAccount, 100).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateTransactionUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateTransactionUseCase.Response.Success(transaction) }
    }

    @Test
    fun createTransaction_whenTransactionFails_returnsFailureResponse() {
        // GIVEN
        val e = Exception()
        whenever(transactionRepository.createTransaction(transaction.copy(id = Transaction.Id.UNSAVED)))
                .thenReturn(Single.error(e))

        // WHEN
        val result = testSubject.createTransaction(fromAccount, toAccount, 100).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateTransactionUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateTransactionUseCase.Response.Failure(e) }
    }
}