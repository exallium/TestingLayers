package com.exallium.testinglayers.domain

import com.exallium.testinglayers.entities.Transaction
import io.reactivex.Single

interface TransactionRepository {
    fun createTransaction(transaction: Transaction): Single<Transaction>
}