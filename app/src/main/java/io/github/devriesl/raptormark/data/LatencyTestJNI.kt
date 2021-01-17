package io.github.devriesl.raptormark.data

class LatencyTestJNI : TestBaseJNI() {
    override fun getTestName(): String {
        return TEST_NAME
    }

    companion object {
        const val TEST_NAME = "Asynchronous Read/Write Latency"
    }
}