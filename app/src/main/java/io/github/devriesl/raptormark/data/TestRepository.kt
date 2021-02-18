package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.di.StringProvider

abstract class TestRepository(
    val stringProvider: StringProvider
) {
    abstract fun getTestName(): String
}