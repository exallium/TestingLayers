package com.exallium.testinglayers.entities

import android.support.v4.util.Preconditions

data class Transaction(val id: Id, val fromAccount: Account.Id, val toAccount: Account.Id, val amount: Long) {
    data class Id(val value: String) {
        companion object {
            val UNSAVED = Id("")
        }
    }

    init {
        Preconditions.checkArgument(amount > 0, "Amount must be positive")
        Preconditions.checkArgument(fromAccount != Account.Id.UNSAVED)
        Preconditions.checkArgument(toAccount != Account.Id.UNSAVED)
        Preconditions.checkArgument(fromAccount != toAccount, "Accounts must be different")
    }
}