package io.github.devriesl.raptormark.data

abstract class TestBaseJNI {
    abstract fun getTestName(): String

    protected open external fun native_ListEngines(): String

    companion object {
        // Used to load the 'fio-jni' library on application startup.
        init {
            System.loadLibrary("fio-jni")
        }
    }
}