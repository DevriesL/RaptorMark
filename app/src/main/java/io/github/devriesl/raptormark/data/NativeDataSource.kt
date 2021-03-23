package io.github.devriesl.raptormark.data

import android.os.Handler
import android.os.HandlerThread
import android.os.Process.THREAD_PRIORITY_FOREGROUND

object NativeDataSource {
    private val nativeThread: HandlerThread =
        HandlerThread(javaClass.simpleName, THREAD_PRIORITY_FOREGROUND)
    private val nativeHandler: Handler

    external fun native_PipeStdLogcat()
    external fun native_FIOTest(jsonCommand: String): Int
    external fun native_LatencyTest(jsonCommand: String): Int
    external fun native_ListEngines(): String

    init {
        System.loadLibrary("fio-jni")

        nativeThread.priority = Thread.MAX_PRIORITY
        nativeThread.start()
        nativeHandler = Handler(nativeThread.looper)

        startPipeStdLogcat()
    }

    fun postNativeThread(block: () -> Unit) {
        nativeHandler.post { block() }
    }

    private fun startPipeStdLogcat() {
        object : Thread() {
            override fun run() {
                native_PipeStdLogcat()
            }
        }.start()
    }
}