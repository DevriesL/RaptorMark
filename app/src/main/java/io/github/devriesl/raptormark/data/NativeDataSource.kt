package io.github.devriesl.raptormark.data

object NativeDataSource {

    external fun native_FIOTest(jsonCommand: String)
    external fun native_LatencyTest(jsonCommand: String)
    external fun native_ListEngines(): String

    init {
        System.loadLibrary("fio-jni")
    }
}