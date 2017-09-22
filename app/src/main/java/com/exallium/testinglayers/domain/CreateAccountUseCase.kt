package com.exallium.testinglayers.domain

import com.exallium.testinglayers.entities.Account
import com.exallium.testinglayers.entities.Customer
import io.reactivex.Observable

class CreateAccountUseCase(private val accountRepository: AccountRepository,
                           private val customerRepository: CustomerRepository) {

    sealed class Response {
        data class Success(val account: Account): Response()
        data class Failure(val throwable: Throwable): Response()
        object CustomerDoesNotExistInDatabase : Response()
        object InProgress : Response()
    }

    fun createAccountForCustomer(customer: Customer): Observable<Response> {
        return customerRepository
                .getCustomerById(customer.id)
                .flatMap {
                    accountRepository.createAccount(Account(Account.Id.UNSAVED, customer.id, 0)).toMaybe()
                }
                .map<Response> { Response.Success(it) }
                .onErrorReturn { Response.Failure(it) }
                .toObservable()
                .defaultIfEmpty(Response.CustomerDoesNotExistInDatabase)
                .startWith(Response.InProgress)
    }

}