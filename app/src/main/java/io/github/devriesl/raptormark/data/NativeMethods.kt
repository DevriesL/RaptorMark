package io.github.devriesl.raptormark.data

object NativeMethods {

    external fun native_ListEngines(): String

    init {
        System.loadLibrary("fio-jni")
    }
}