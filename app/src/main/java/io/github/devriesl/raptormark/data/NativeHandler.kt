package io.github.devriesl.raptormark.data

import android.os.Handler
import android.os.HandlerThread
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import androidx.annotation.Keep

object NativeHandler {
    private val nativeThread: HandlerThread =
        HandlerThread(javaClass.simpleName, THREAD_PRIORITY_FOREGROUND)
    private val nativeHandler: Handler

    private val listeners: HashSet<NativeListener> = hashSetOf()

    external fun native_MBWTest(jsonCommand: String): Int
    external fun native_FIOTest(jsonCommand: String): Int
    external fun native_ListEngines(): String

    init {
        System.loadLibrary("raptormark-jni")

        nativeThread.priority = Thread.MAX_PRIORITY
        nativeThread.start()
        nativeHandler = Handler(nativeThread.looper)
    }

    fun postNativeThread(block: () -> Unit) {
        nativeHandler.post { block() }
    }

    fun registerListener(listener: NativeListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: NativeListener) {
        listeners.remove(listener)
    }

    @Keep
    @JvmStatic
    private fun updateStatus(msg: String): Int {
        listeners.forEach {
            it.onTestResult(msg)
        }
        return msg.length
    }
}
