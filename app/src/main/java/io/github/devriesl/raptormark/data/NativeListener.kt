package io.github.devriesl.raptormark.data

interface NativeListener {
    fun onTestResult(vararg results: Int)
}