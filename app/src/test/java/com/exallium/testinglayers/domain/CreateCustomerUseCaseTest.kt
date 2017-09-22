package com.exallium.testinglayers.domain

import com.exallium.testinglayers.entities.Customer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Test

class CreateCustomerUseCaseTest {
    private val customerRepository: CustomerRepository = mock()
    private val testSubject = CreateCustomerUseCase(customerRepository)

    @Test
    fun createCustomerWithDisplayName_withEmptyDisplayName_returnsEmptyDisplayNameResponse() {
        // WHEN
        val result = testSubject.createCustomerWithDisplayName("").test()

        // THEN
        result
                .assertValueAt(0) { it == CreateCustomerUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateCustomerUseCase.Response.EmptyDisplayName }
    }

    @Test
    fun createCustomerWithDisplayName_whenSuccess_returnsSuccessResponse() {
        // GIVEN
        val displayName = "asdf"
        val customer = Customer(Customer.Id("asdf"), displayName)
        whenever(customerRepository.createCustomer(any())).thenReturn(Single.just(customer))

        // WHEN
        val result = testSubject.createCustomerWithDisplayName(displayName).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateCustomerUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateCustomerUseCase.Response.Success(customer) }
    }

    @Test
    fun createCustomerWithDisplayName_whenFailure_returnsFailureResponse() {
        // GIVEN
        val displayName = "asdf"
        val e = Exception()
        whenever(customerRepository.createCustomer(any())).thenReturn(Single.error(e))

        // WHEN
        val result = testSubject.createCustomerWithDisplayName(displayName).test()

        // THEN
        result
                .assertValueAt(0) { it == CreateCustomerUseCase.Response.InProgress }
                .assertValueAt(1) { it == CreateCustomerUseCase.Response.Failure(e) }
    }
}