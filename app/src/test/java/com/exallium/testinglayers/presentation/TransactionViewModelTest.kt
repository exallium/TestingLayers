package com.exallium.testinglayers.presentation

import com.exallium.testinglayers.domain.AccountRepository
import com.exallium.testinglayers.domain.CreateTransactionUseCase
import com.exallium.testinglayers.domain.CustomerRepository
import com.exallium.testinglayers.domain.TransactionRepository
import com.exallium.testinglayers.entities.Account
import com.exallium.testinglayers.entities.Customer
import com.exallium.testinglayers.entities.Transaction
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test

class TransactionViewModelTest {
    val account = Account(Account.Id("id1"), Customer.Id("asdf"), 100)
    val customer = Customer(Customer.Id("asdf"), "asdf")
    val accountRepository: AccountRepository = mock()
    val customerRepository: CustomerRepository = mock()
    val transactionRepository: TransactionRepository = mock()
    val createTransactionUseCase = CreateTransactionUseCase(accountRepository, transactionRepository)
    val router: Router = mock()
    lateinit var testSubject: TransactionViewModel

    @Before
    fun setUp() {
        whenever(customerRepository.getCustomerById(any())).thenReturn(Maybe.just(customer))
        whenever(accountRepository.getAccountById(any())).thenAnswer {
            Maybe.just(Account(it.arguments.first() as Account.Id, customer.id, 100))
        }
        whenever(accountRepository.getAccountsForCustomer(any())).thenReturn(Observable.just(listOf()))
        whenever(accountRepository.updateAccount(any())).thenReturn(Single.just(account))
        whenever(transactionRepository.createTransaction(any())).thenReturn(
                Single.just(Transaction(Transaction.Id("asdf"), account.id, Account.Id("id2"), 10)))
        testSubject = TransactionViewModel(account, accountRepository, customerRepository, createTransactionUseCase, router)
    }

    @Test
    fun otherAccounts_doesNotIncludeAccount() {
        // GIVEN
        val accounts = (1..10).map { Account(Account.Id("id$it"), customer.id, 100) }
        whenever(accountRepository.getAccountsForCustomer(any())).thenReturn(Observable.just(accounts))
        val ts = TransactionViewModel(account, accountRepository, customerRepository, createTransactionUseCase, router)

        // WHEN
        val result = ts.otherAccounts.test()

        // THEN
        result.assertValues(accounts.drop(1))
    }

    @Test
    fun confirm_whenToAccountNotFound_setsAccountErrorMessage() {
        // GIVEN
        whenever(accountRepository.getAccountById(any())).thenReturn(Maybe.empty())

        // WHEN
        testSubject.confirm()

        // THEN
        assertTrue(testSubject.selectedOtherAccountError.get().isNotEmpty())
    }

    @Test
    fun confirm_whenNonPositiveAmount_setsAmountErrorMessage() {
        // GIVEN
        testSubject.amount.set(0)
        testSubject.selectedOtherAccount.set("asdf")

        // WHEN
        testSubject.confirm()

        // THEN
        assertTrue(testSubject.amountError.get().isNotEmpty())
    }

    @Test
    fun confirm_whenInsufficientFunds_setsAmountErrorMessage() {
        // GIVEN
        testSubject.amount.set(1000)
        testSubject.selectedOtherAccount.set("asdf")

        // WHEN
        testSubject.confirm()

        // THEN
        assertTrue(testSubject.amountError.get().isNotEmpty())
    }

    @Test
    fun confirm_whenFailure_setsGeneralErrorMessage() {
        // GIVEN
        whenever(transactionRepository.createTransaction(any())).thenReturn(Single.error(Exception()))
        testSubject.amount.set(10)
        testSubject.selectedOtherAccount.set("asdf")

        // WHEN
        testSubject.confirm()

        // THEN
        assertTrue(testSubject.generalError.get().isNotEmpty())
    }

    @Test
    fun confirm_whenSuccess_goBack() {
        // GIVEN
        testSubject.amount.set(10)
        testSubject.selectedOtherAccount.set("asdf")

        // WHEN
        testSubject.confirm()

        // THEN
        verify(router).goBack()
    }
}