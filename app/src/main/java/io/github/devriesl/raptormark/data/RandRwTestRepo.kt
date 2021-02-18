package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider

class RandRwTestRepo(stringProvider: StringProvider) : TestRepository(stringProvider) {
    override fun getTestName(): String {
        return stringProvider.getString(R.string.rand_rw_test_title)
    }
}