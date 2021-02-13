package io.github.devriesl.raptormark.data

abstract class TestBaseJNI {
    abstract fun getTestName(): String

    companion object {
        // Used to load the 'fio-jni' library on application startup.
        init {
            System.loadLibrary("fio-jni")
        }
    }
}