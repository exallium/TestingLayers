package com.exallium.testinglayers.entities

import org.junit.Test

class TransactionTest {
    @Test(expected = IllegalArgumentException::class)
    fun transaction_withSameAccount_throwsIllegalArgumentException() {
        // WHEN
        Transaction(Transaction.Id("asdf"), Account.Id("asdf"), Account.Id("asdf"), 100)
    }

    @Test(expected = IllegalArgumentException::class)
    fun transaction_withNonPositiveAmount_throwsIllegalArgumentException() {
        // WHEN
        Transaction(Transaction.Id("asdf"), Account.Id("asdf2"), Account.Id("asdf"), 0)
    }

    @Test
    fun transaction_withPositiveAmountAndDifferentAccounts_doesNotThrow() {
        // WHEN
        Transaction(Transaction.Id("asdf"), Account.Id("asdf2"), Account.Id("asdf"), 100)
    }

    @Test(expected = IllegalArgumentException::class)
    fun transaction_withUnsavedFromAccount_throwsIllegalArgumentException() {
        // WHEN
        Transaction(Transaction.Id("asdf"), Account.Id.UNSAVED, Account.Id("asdf"), 100)
    }

    @Test(expected = IllegalArgumentException::class)
    fun transaction_withUnsavedToAccount_throwsIllegalArgumentException() {
        // WHEN
        Transaction(Transaction.Id("asdf"), Account.Id("asdf"), Account.Id.UNSAVED, 100)
    }
}
