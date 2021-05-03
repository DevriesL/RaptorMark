package io.github.devriesl.raptormark.data

import android.os.Handler
import android.os.HandlerThread
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import android.util.Log
import androidx.annotation.Keep
import org.json.JSONObject

object NativeDataSource {
    private val nativeThread: HandlerThread =
        HandlerThread(javaClass.simpleName, THREAD_PRIORITY_FOREGROUND)
    private val nativeHandler: Handler

    private val listeners: HashSet<NativeListener> = hashSetOf()

    external fun native_FIOTest(jsonCommand: String): Int
    external fun native_LatencyTest(jsonCommand: String): Int
    external fun native_ListEngines(): String

    init {
        System.loadLibrary("fio-jni")

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
        parseJsonResult(msg)
        return msg.length
    }

    private fun parseJsonResult(msg: String) {
        var jobsId: String? = null
        var jobsRw = String()
        var sumOfBwBytes: Long = 0
        var sumOf4NClatNs: Long = 0

        val jsonResult = JSONObject(msg)
        val jobsArray = jsonResult.getJSONArray("jobs")
        for (i in 0 until jobsArray.length()) {
            val jobObject: JSONObject = jobsArray.getJSONObject(i)

            if (jobsId.isNullOrEmpty()) {
                jobsId = jobObject.getString("jobname")
                when {
                    jobsId.contains("rd") -> {
                        jobsRw = "read"
                    }
                    jobsId.contains("wr") -> {
                        jobsRw = "write"
                    }
                    else -> {
                        Log.e(TAG, "Unknown jobs RW type:$jobsId")
                    }
                }
            }

            val rwObject: JSONObject = jobObject.getJSONObject(jobsRw)
            sumOfBwBytes += rwObject.getLong("bw_bytes")
            val clatObject: JSONObject = rwObject.getJSONObject("clat_ns")
            val percentileObject: JSONObject = clatObject.getJSONObject("percentile")
            sumOf4NClatNs += percentileObject.getLong("99.990000")
        }

        val sumOfBw = (sumOfBwBytes / 1000 / 1000).toInt()
        val avgOf4NClat = (sumOf4NClatNs / jobsArray.length() / 1000).toInt()

        Log.i(TAG, jobsId + ": bw=" + sumOfBw + "MB/s, 99.99% Latency=" + avgOf4NClat + "µs")

        listeners.forEach { it.onTestResult(sumOfBw, avgOf4NClat) }
    }

    private const val TAG = "Native_DataSrc"
}