package com.exallium.testinglayers.entities

import android.support.v4.util.Preconditions

data class Account(val id: Id, val owner: Customer.Id, val balance: Long) {
    data class Id(val value: String) {
        companion object {
            val UNSAVED = Id("")
        }
    }

    init {
        Preconditions.checkArgument(balance >= 0)
        Preconditions.checkArgument(owner != Customer.Id.UNSAVED)
    }
}