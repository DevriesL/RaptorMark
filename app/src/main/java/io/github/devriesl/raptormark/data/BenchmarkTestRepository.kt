package io.github.devriesl.raptormark.data

class BenchmarkTestRepository(private val nativeTest: FIONativeTest) {
    fun getName() = nativeTest.getTestName()
}