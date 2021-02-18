package io.github.devriesl.raptormark.data

class RandRwTestRepo : TestRepository() {
    override fun getTestName(): String {
        return TEST_NAME
    }

    companion object {
        const val TEST_NAME = "Random Read/Write Throughput"
    }
}