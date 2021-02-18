package io.github.devriesl.raptormark.data

class SeqRwTestRepo : TestRepository() {
    override fun getTestName(): String {
        return TEST_NAME
    }

    companion object {
        const val TEST_NAME = "Sequential Read/Write Throughput"
    }
}