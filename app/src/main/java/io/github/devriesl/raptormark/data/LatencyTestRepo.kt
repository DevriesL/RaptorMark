package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider

class LatencyTestRepo(stringProvider: StringProvider) : TestRepository(stringProvider) {
    override fun getTestName(): String {
        return stringProvider.getString(R.string.latency_test_title)
    }
}