package com.exallium.testinglayers.domain

import com.exallium.testinglayers.entities.Customer
import io.reactivex.Observable

class CreateCustomerUseCase(private val customerRepository: CustomerRepository) {

    sealed class Response {
        object InProgress : Response()
        object EmptyDisplayName : Response()
        data class Success(val customer: Customer) : Response()
        data class Failure(val throwable: Throwable) : Response()
    }

    fun createCustomerWithDisplayName(displayName: String): Observable<Response> {
        return if (displayName.isEmpty()) {
            Observable.just<Response>(Response.EmptyDisplayName)
        } else {
            customerRepository
                    .createCustomer(Customer(Customer.Id.UNSAVED, displayName))
                    .map<Response> { Response.Success(it) }
                    .onErrorReturn { Response.Failure(it) }
                    .toObservable()
        }.startWith(Response.InProgress)
    }
}