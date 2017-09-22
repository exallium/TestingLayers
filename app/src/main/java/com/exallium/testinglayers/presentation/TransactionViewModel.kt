package com.exallium.testinglayers.presentation

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableLong
import com.exallium.testinglayers.domain.AccountRepository
import com.exallium.testinglayers.domain.CreateTransactionUseCase
import com.exallium.testinglayers.domain.CreateTransactionUseCase.Response as R
import com.exallium.testinglayers.domain.CustomerRepository
import com.exallium.testinglayers.entities.Account

class TransactionViewModel(private val account: Account,
                           private val accountRepository: AccountRepository,
                           customerRepository: CustomerRepository,
                           private val createTransactionUseCase: CreateTransactionUseCase,
                           private val router: Router) {

    val otherAccounts = customerRepository
            .getCustomerById(account.owner)
            .flatMapObservable(accountRepository::getAccountsForCustomer)
            .map { it.filter { it.id != account.id } }

    val selectedOtherAccount = ObservableField<String>("")
    val selectedOtherAccountError = ObservableField<String>("")
    val amount = ObservableLong()
    val amountError = ObservableField<String>("")
    val isInProgress = ObservableBoolean(false)
    val generalError = ObservableField<String>("")

    fun confirm() {
        accountRepository
                .getAccountById(Account.Id(selectedOtherAccount.get()))
                .doOnComplete {
                    selectedOtherAccountError.set("Destination Account not Found")
                }
                .flatMapObservable {
                    createTransactionUseCase.createTransaction(account, it, amount.get())
                }
                .subscribe(this::handleResponse)
    }

    private fun handleResponse(response: CreateTransactionUseCase.Response) {
        isInProgress.set(false)
        generalError.set("")
        selectedOtherAccountError.set("")
        amountError.set("")
        when (response) {
            is R.InProgress -> isInProgress.set(true)
            is R.InsufficientFunds -> amountError.set("Insufficient Funds")
            is R.NonPositiveAmount -> amountError.set("Amount must be positive number")
            is R.UnknownFromAccount -> generalError.set("Invalid Source Account")
            is R.UnknownToAccount -> selectedOtherAccountError.set("Invalid Destination Account")
            is R.Failure -> generalError.set("Something bad happened!")
            is R.Success -> router.goBack()
        }
    }

}