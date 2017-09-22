package com.exallium.testinglayers.presentation

import com.exallium.testinglayers.domain.AccountRepository
import com.exallium.testinglayers.domain.CreateAccountUseCase
import com.exallium.testinglayers.domain.CustomerRepository
import com.exallium.testinglayers.entities.Account
import com.exallium.testinglayers.entities.Customer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AccountsListViewModelTest {
    val customer = Customer(Customer.Id("asdf"), "asdf")
    val customerRepository: CustomerRepository = mock()
    val accountRepository: AccountRepository = mock()
    val createAccountUseCase = CreateAccountUseCase(accountRepository, customerRepository)
    val router: Router = mock()
    lateinit var testSubject: AccountsListViewModel

    @Before
    fun setUp() {
        whenever(customerRepository.getCustomerById(customer.id)).thenReturn(Maybe.just(customer))
        whenever(accountRepository.getAccountsForCustomer(customer)).thenReturn(Observable.just(listOf()))
        testSubject = AccountsListViewModel(customer, customerRepository, accountRepository, createAccountUseCase, router)
    }

    @Test
    fun accounts_whenAccountsAvailable_wrappersMatchSize() {
        // GIVEN
        val account = Account(Account.Id("asdf"), customer.id, 0)
        val accounts = listOf(account, account, account, account, account)
        whenever(accountRepository.getAccountsForCustomer(customer)).thenReturn(Observable.just(accounts))

        // WHEN
        val result = testSubject.accounts.test()

        // THEN
        result.assertValue { it.size == accounts.size }
    }

    @Test
    fun init_noErrorMessageAndButtonEnabled() {
        // WHEN
        testSubject.accounts.subscribe()

        // THEN
        assertTrue(testSubject.errorMessage.get().isEmpty())
        assertTrue(testSubject.isAddEnabled.get())
    }

    @Test
    fun init_whenCustomerDNE_errorMessageAndButtonDisabled() {
        // GIVEN
        whenever(customerRepository.getCustomerById(customer.id)).thenReturn(Maybe.empty())
        val ts = AccountsListViewModel(customer, customerRepository, accountRepository, createAccountUseCase, router)

        // WHEN
        ts.accounts.subscribe()

        // THEN
        assertTrue(ts.errorMessage.get().isNotEmpty())
        assertFalse(ts.isAddEnabled.get())
    }

    @Test
    fun addAccount_whenCustomerDNE_showsErrorMessageAndDisablesAddButton() {
        // GIVEN
        whenever(customerRepository.getCustomerById(customer.id)).thenReturn(Maybe.empty())

        // WHEN
        testSubject.addAccount()

        // THEN
        assertTrue(testSubject.errorMessage.get().isNotEmpty())
        assertFalse(testSubject.isAddEnabled.get())
    }

    @Test
    fun addAccount_whenFailed_showsErrorMessage() {
        // GIVEN
        whenever(accountRepository.createAccount(any())).thenReturn(Single.error(Exception()))

        // WHEN
        testSubject.addAccount()

        // THEN
        assertTrue(testSubject.errorMessage.get().isNotEmpty())
        assertTrue(testSubject.isAddEnabled.get())
    }
}