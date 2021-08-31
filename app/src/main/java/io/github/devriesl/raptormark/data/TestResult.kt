package io.github.devriesl.raptormark.data

import org.json.JSONObject

data class TestResult(
    val bandwidth: Int? = null,
    val latency: Int? = null,
)

fun parseTestResult(result: String): TestResult {
    var jobsId: String? = null
    var jobsRw = String()
    var sumOfBwBytes: Long = 0
    var sumOf4NClatNs: Long = 0

    val jsonResult = JSONObject(result)
    val jobsArray = jsonResult.getJSONArray("jobs")
    for (i in 0 until jobsArray.length()) {
        val jobObject: JSONObject = jobsArray.getJSONObject(i)

        if (jobsId.isNullOrEmpty()) {
            jobsId = jobObject.getString("jobname")
            when {
                jobsId.contains("RD") -> {
                    jobsRw = "read"
                }
                jobsId.contains("WR") -> {
                    jobsRw = "write"
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

    return TestResult(sumOfBw, avgOf4NClat)
}
