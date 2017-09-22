package com.exallium.testinglayers.entities

import org.junit.Test

class CustomerTest {
    @Test(expected = IllegalArgumentException::class)
    fun customer_withEmptyName_throwsIllegalArgumentException() {
        // WHEN
        Customer(Customer.Id("asdf"), "")
    }

    @Test
    fun customer_withNonEmptyName_doesNotThrow() {
        // WHEN
        Customer(Customer.Id("asdf"), "asdf")
    }
}