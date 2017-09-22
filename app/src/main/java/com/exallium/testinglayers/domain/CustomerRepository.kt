package com.exallium.testinglayers.domain

import com.exallium.testinglayers.entities.Customer
import io.reactivex.Maybe
import io.reactivex.Single

interface CustomerRepository {
    fun createCustomer(customer: Customer): Single<Customer>
    fun getCustomerById(id: Customer.Id): Maybe<Customer>
}
