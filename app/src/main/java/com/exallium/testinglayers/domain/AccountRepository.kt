package com.exallium.testinglayers.domain

import com.exallium.testinglayers.entities.Account
import com.exallium.testinglayers.entities.Customer
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

interface AccountRepository {
    fun getAccountsForCustomer(customer: Customer): Observable<List<Account>>
    fun getAccountById(id: Account.Id): Maybe<Account>
    fun createAccount(account: Account): Single<Account>
    fun updateAccount(account: Account): Single<Account>
}