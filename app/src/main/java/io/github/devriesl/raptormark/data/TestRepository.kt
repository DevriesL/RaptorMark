package io.github.devriesl.raptormark.data

import androidx.lifecycle.MutableLiveData
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
import io.github.devriesl.raptormark.Constants.TEST_FILE_NAME_SUFFIX
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

abstract class TestRepository constructor(
    val stringProvider: StringProvider,
    val settingDataSource: SettingDataSource,
    val historyDatabase: HistoryDatabase
) {
    abstract val testFileName: String
    open var testTypeValue: String = String()
    open var testResult: Int = 0
    open var testResultMutableLiveData: MutableLiveData<String> = MutableLiveData<String>()
    open var isRandTest: Boolean = false
    open var randLatResult: Int = 0
    open var randLatResultMutableLiveData: MutableLiveData<String> = MutableLiveData<String>()

    private val nativeListener = object : NativeListener {
        override fun onTestResult(vararg results: Int) {
            this@TestRepository.onTestResult(results = results)
        }
    }

    abstract fun getTestName(): String

    abstract fun onTestResult(vararg results: Int)

    open fun runTest() {
        NativeDataSource.registerListener(nativeListener)
        val options = testOptionsBuilder()
        val ret = NativeDataSource.native_FIOTest(options)
        val testFile = File(getTestFilePath())
        if (testFile.exists()) testFile.delete()
        NativeDataSource.unregisterListener(nativeListener)

        if (ret != 0) {
            throw IOException("$ret")
        } else {
            updateTestResult()
        }
    }

    open fun updateTestResult() {
        testResultMutableLiveData.postValue(
            stringProvider.getString(
                R.string.sum_of_bw_test_result_format,
                testResult
            )
        )
        randLatResultMutableLiveData.postValue(
            stringProvider.getString(
                R.string.avg_of_4n_lat_result_format,
                randLatResult
            )
        )
    }

    private fun getTestFilePath(): String {
        return settingDataSource.getAppStoragePath() + "/" + testFileName + TEST_FILE_NAME_SUFFIX
    }

    private fun testOptionsBuilder(): String {
        val root = JSONObject()
        val options = JSONArray()

        root.put("shortopts", false)

        options.put(createOption(NEW_JOB_OPT_NAME, testFileName))
        options.put(createOption(FILE_PATH_OPT_NAME, getTestFilePath()))
        options.put(createOption(IO_TYPE_OPT_NAME, testTypeValue))
        options.put(createOption(IO_DEPTH_OPT_NAME, DEFAULT_IO_DEPTH_VALUE))
        options.put(createOption(RUNTIME_OPT_NAME, DEFAULT_RUNTIME_LIMIT_VALUE))
        options.put(
            createOption(
                BLOCK_SIZE_OPT_NAME,
                if (isRandTest) DEFAULT_RAND_BLOCK_SIZE_VALUE else DEFAULT_SEQ_BLOCK_SIZE_VALUE
            )
        )
        options.put(createOption(IO_SIZE_OPT_NAME, DEFAULT_IO_SIZE_VALUE))
        options.put(createOption(DIRECT_IO_OPT_NAME, CONSTANT_DIRECT_IO_VALUE))
        options.put(createOption(IO_ENGINE_OPT_NAME, settingDataSource.getEngineConfig()))
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
        const val SUM_OF_BW_RESULT_INDEX = 0
        const val AVG_OF_4N_LAT_RESULT_INDEX = 1
    }
}