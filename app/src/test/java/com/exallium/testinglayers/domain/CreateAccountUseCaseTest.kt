package com.exallium.testinglayers.domain

import com.exallium.testinglayers.entities.Account
import com.exallium.testinglayers.entities.Customer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Test

class CreateAccountUseCaseTest {

    private val accountRepository: AccountRepository = mock()
    private val customerRepository: CustomerRepository = mock()
    private val testSubject = CreateAccountUseCase(accountRepository, customerRepository)

    @Test
    fun createAccountForCustomer_whenCustomerDoesNotExistInDB_returnsCustomerDoesNotExistInDatabase() {
        // GIVEN
        whenever(customerRepository.getCustomerById(any())).thenReturn(Maybe.empty())
        val customer = Customer(Customer.Id.UNSAVED, "asdf")

        // WHEN
        val result = testSubject.createAccountForCustomer(customer).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateAccountUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateAccountUseCase.Response.CustomerDoesNotExistInDatabase }
    }

    @Test
    fun createAccountForCustomer_onSuccess_returnsAccountWithId() {
        // GIVEN
        val customer = Customer(Customer.Id("asdf"), "asdf")
        val id = Account.Id("asdf")
        val account = Account(id, customer.id, 0)
        whenever(customerRepository.getCustomerById(any())).thenReturn(Maybe.just(customer))
        whenever(accountRepository.createAccount(any())).thenReturn(Single.just(account))

        // WHEN
        val result = testSubject.createAccountForCustomer(customer).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateAccountUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateAccountUseCase.Response.Success(account) }
    }

    @Test
    fun createAccountForCustomer_onFailure_returnsFailureWithThrowable() {
        // GIVEN
        val customer = Customer(Customer.Id("asdf"), "asdf")
        val e = Exception()
        whenever(customerRepository.getCustomerById(any())).thenReturn(Maybe.just(customer))
        whenever(accountRepository.createAccount(any())).thenReturn(Single.error(e))

        // WHEN
        val result = testSubject.createAccountForCustomer(customer).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateAccountUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateAccountUseCase.Response.Failure(e) }

    }
}