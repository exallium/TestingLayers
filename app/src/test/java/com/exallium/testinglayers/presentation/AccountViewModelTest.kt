package com.exallium.testinglayers.presentation

import com.exallium.testinglayers.entities.Account
import com.exallium.testinglayers.entities.Customer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class AccountViewModelTest {
    val router: Router = mock()
    val account = Account(Account.Id("acc1"), Customer.Id("cust1"), 0)

    val testSubject = AccountViewModel(account, router)

    @Test
    fun click_startTransaction() {
        // WHEN
        testSubject.click()

        // THEN
        verify(router).startTransaction(account)
    }

    @Test
    fun sameAs_hasSameId_isSame() {
        // GIVEN
        val other = AccountViewModel(account.copy(balance = 100), router)

        // WHEN
        val result = testSubject.sameAs(other)

        // THEN
        assert(result)
    }

    @Test
    fun sameAs_notSameId_isNotSame() {
        // GIVEN
        val other = AccountViewModel(account.copy(id = Account.Id.UNSAVED), router)

        // WHEN
        val result = testSubject.sameAs(other)

        // THEN
        assert(!result)
    }

    @Test
    fun sameContentsAs_isSameContents_returnsTrue() {
        // GIVEN
        val other = testSubject

        // WHEN
        val result = testSubject.sameContentsAs(other)

        // THEN
        assert(result)
    }

    @Test
    fun sameContentsAs_isNotSameContents_returnsFalse() {
        // GIVEN
        val other = AccountViewModel(account.copy(balance = 100), router)

        // WHEN
        val result = testSubject.sameContentsAs(other)

        // THEN
        assert(!false)
    }
}