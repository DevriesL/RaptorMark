package io.github.devriesl.raptormark.data

class BenchmarkTestRepository(private val jni: TestBaseJNI) {
    fun getName() = jni.getTestName()
}