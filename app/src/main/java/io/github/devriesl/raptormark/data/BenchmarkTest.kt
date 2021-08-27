package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.Constants.BLOCK_SIZE_OPT_NAME
import io.github.devriesl.raptormark.Constants.CONSTANT_DIRECT_IO_VALUE
import io.github.devriesl.raptormark.Constants.CONSTANT_ETA_PRINT_VALUE
import io.github.devriesl.raptormark.Constants.CONSTANT_OUTPUT_FORMAT_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_IO_DEPTH_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_IO_SIZE_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_NUM_THREADS_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_RAND_BLOCK_SIZE_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_RUNTIME_LIMIT_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_SEQ_BLOCK_SIZE_VALUE
import io.github.devriesl.raptormark.Constants.DIRECT_IO_OPT_NAME
import io.github.devriesl.raptormark.Constants.ETA_PRINT_OPT_NAME
import io.github.devriesl.raptormark.Constants.FILE_PATH_OPT_NAME
import io.github.devriesl.raptormark.Constants.IO_DEPTH_OPT_NAME
import io.github.devriesl.raptormark.Constants.IO_ENGINE_OPT_NAME
import io.github.devriesl.raptormark.Constants.IO_SIZE_OPT_NAME
import io.github.devriesl.raptormark.Constants.IO_TYPE_OPT_NAME
import io.github.devriesl.raptormark.Constants.NEW_JOB_OPT_NAME
import io.github.devriesl.raptormark.Constants.NUM_THREADS_OPT_NAME
import io.github.devriesl.raptormark.Constants.OUTPUT_FORMAT_OPT_NAME
import io.github.devriesl.raptormark.Constants.RUNTIME_OPT_NAME
import io.github.devriesl.raptormark.data.SettingOptions.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

class BenchmarkTest constructor(
    val testCases: TestCases,
    private val settingSharedPrefs: SettingSharedPrefs
) {
    private val mutableTestResult = MutableStateFlow(TestResult())

    val testResult: StateFlow<TestResult>
        get() = mutableTestResult

    private fun updateTestResult(vararg results: Int) {
        mutableTestResult.value = TestResult(
            bandwidth = results[SUM_OF_BW_RESULT_INDEX],
            latency = if (testCases.isRand) results[AVG_OF_4N_LAT_RESULT_INDEX] else null
        )
    }

    private val nativeListener = object : NativeListener {
        override fun onTestResult(vararg results: Int) {
            this@BenchmarkTest.updateTestResult(results = results)
        }
    }

    fun runTest() {
        NativeDataSource.registerListener(nativeListener)
        val options = testOptionsBuilder()
        val ret = NativeDataSource.native_FIOTest(options)
        val testFile = File(getTestFilePath())
        if (testFile.exists()) testFile.delete()
        NativeDataSource.unregisterListener(nativeListener)

        if (ret != 0) {
            throw IOException("$ret")
        }
    }

    private fun getTestFilePath(): String {
        return settingSharedPrefs.getTestDirPath() + "/" + testCases.name + TEST_FILE_NAME_SUFFIX
    }

    private fun testOptionsBuilder(): String {
        val root = JSONObject()
        val options = JSONArray()

        root.put("shortopts", false)

        options.put(createOption(NEW_JOB_OPT_NAME, testCases.name))
        options.put(createOption(FILE_PATH_OPT_NAME, getTestFilePath()))
        options.put(createOption(IO_TYPE_OPT_NAME, testCases.type))
        options.put(createOption(IO_DEPTH_OPT_NAME, DEFAULT_IO_DEPTH_VALUE))
        options.put(createOption(RUNTIME_OPT_NAME, DEFAULT_RUNTIME_LIMIT_VALUE))
        options.put(
            createOption(
                BLOCK_SIZE_OPT_NAME,
                if (testCases.isRand) DEFAULT_RAND_BLOCK_SIZE_VALUE else DEFAULT_SEQ_BLOCK_SIZE_VALUE
            )
        )
        options.put(createOption(IO_SIZE_OPT_NAME, DEFAULT_IO_SIZE_VALUE))
        options.put(createOption(DIRECT_IO_OPT_NAME, CONSTANT_DIRECT_IO_VALUE))
        options.put(
            createOption(
                IO_ENGINE_OPT_NAME,
                ENGINE_CONFIG.settingData.getSettingData(settingSharedPrefs)
            )
        )
        options.put(createOption(NUM_THREADS_OPT_NAME, DEFAULT_NUM_THREADS_VALUE))

        options.put(createOption(ETA_PRINT_OPT_NAME, CONSTANT_ETA_PRINT_VALUE))
        options.put(createOption(OUTPUT_FORMAT_OPT_NAME, CONSTANT_OUTPUT_FORMAT_VALUE))

        root.put("options", options)

        return root.toString()
    }

    private fun createOption(name: String, value: String): JSONObject {
        val jsonOption = JSONObject()
        jsonOption.put("name", name)
        jsonOption.put("value", value)
        return jsonOption
    }

    companion object {
        const val TEST_FILE_NAME_SUFFIX = ".tmp"

        const val SUM_OF_BW_RESULT_INDEX = 0
        const val AVG_OF_4N_LAT_RESULT_INDEX = 1
    }
}

data class TestResult(
    val bandwidth: Int? = null,
    val latency: Int? = null,
)
