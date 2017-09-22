package com.exallium.testinglayers.presentation

import com.exallium.testinglayers.entities.Account
import com.exallium.testinglayers.entities.Customer

interface Router {
    fun goToAccounts(customer: Customer)
    fun startTransaction(account: Account)
    fun goBack()
}