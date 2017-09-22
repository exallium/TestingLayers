package com.exallium.testinglayers.presentation

import com.exallium.testinglayers.domain.CreateCustomerUseCase
import com.exallium.testinglayers.domain.CustomerRepository
import com.exallium.testinglayers.entities.Customer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class CreateCustomerViewModelTest {
    val customerRepository: CustomerRepository = mock()
    val createCustomerUseCase: CreateCustomerUseCase = CreateCustomerUseCase(customerRepository)
    val router: Router = mock()
    val testSubject = CreateCustomerViewModel(createCustomerUseCase, router)
    val customer = Customer(Customer.Id("asdf"), "asdf")

    @Before
    fun setUp() {
        whenever(customerRepository.createCustomer(any())).thenReturn(Single.just(customer))
    }

    @Test
    fun init_defaultState() {
        // THEN
        assert(testSubject.displayName.get().isEmpty())
        assert(testSubject.errorMessage.get().isEmpty())
        assert(!testSubject.isInProgress.get())
    }

    @Test
    fun createCustomer_withEmptyDisplayName_showsError() {
        // WHEN
        testSubject.createCustomer()

        // THEN
        assert(testSubject.errorMessage.get().isNotEmpty())
    }

    @Test
    fun createCustomer_withDisplayName_routesToAccounts() {
        // GIVEN
        testSubject.displayName.set("asdf")

        // WHEN
        testSubject.createCustomer()

        // THEN
        verify(router).goToAccounts(any())
    }

    @Test
    fun createCustomer_withRepoFailure_showsError() {
        // GIVEN
        testSubject.displayName.set("asdf")
        whenever(customerRepository.createCustomer(any())).thenReturn(Single.error(Exception()))

        // WHEN
        testSubject.createCustomer()

        // THEN
        assert(testSubject.errorMessage.get().isNotEmpty())
    }
}