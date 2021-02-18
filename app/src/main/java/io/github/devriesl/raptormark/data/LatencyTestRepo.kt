package io.github.devriesl.raptormark.data

class LatencyTestRepo : TestRepository() {
    override fun getTestName(): String {
        return TEST_NAME
    }

    companion object {
        const val TEST_NAME = "Asynchronous Read/Write Latency"
    }
}