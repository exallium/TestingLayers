package com.exallium.testinglayers.presentation

interface Diffable<in T : Diffable<T>> {
    fun sameAs(other: T): Boolean
    fun sameContentsAs(other: T): Boolean
}