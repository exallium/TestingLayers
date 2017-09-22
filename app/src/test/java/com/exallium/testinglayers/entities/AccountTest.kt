package com.exallium.testinglayers.entities

import org.junit.Test

class AccountTest {
    @Test(expected = IllegalArgumentException::class)
    fun account_withNegativeBalance_throwsIllegalArgumentException() {
        // WHEN
        Account(Account.Id("asdf"), Customer.Id("asdf"), -1)
    }

    @Test
    fun account_withNonNegativeBalance_doesNotThrow() {
        // WHEN
        Account(Account.Id("asdf"), Customer.Id("asdf"), 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun account_withUnsavedOwner_throwsIllegalArgumentException() {
        // WHEN
        Account(Account.Id("asdf"), Customer.Id.UNSAVED, 0)
    }
}