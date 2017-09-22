package com.exallium.testinglayers.entities

import android.support.v4.util.Preconditions

data class Customer(val id: Id, val displayName: String) {
    data class Id(val value: String) {
        companion object {
            val UNSAVED = Id("")
        }
    }

    init {
        Preconditions.checkArgument(displayName.isNotEmpty())
    }
}