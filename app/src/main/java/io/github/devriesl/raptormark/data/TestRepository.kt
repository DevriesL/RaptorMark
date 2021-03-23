package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.Constants.BLOCK_SIZE_OPT_NAME
import io.github.devriesl.raptormark.Constants.DEFAULT_RAND_BLOCK_SIZE_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_SEQ_BLOCK_SIZE_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_IO_DEPTH_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_IO_SIZE_VALUE
import io.github.devriesl.raptormark.Constants.DEFAULT_RUNTIME_LIMIT
import io.github.devriesl.raptormark.Constants.DIRECT_IO_CONSTANT_VALUE
import io.github.devriesl.raptormark.Constants.DIRECT_IO_OPT_NAME
import io.github.devriesl.raptormark.Constants.FILE_PATH_OPT_NAME
import io.github.devriesl.raptormark.Constants.IO_DEPTH_OPT_NAME
import io.github.devriesl.raptormark.Constants.IO_ENGINE_OPT_NAME
import io.github.devriesl.raptormark.Constants.IO_SIZE_OPT_NAME
import io.github.devriesl.raptormark.Constants.IO_TYPE_OPT_NAME
import io.github.devriesl.raptormark.Constants.NEW_JOB_OPT_NAME
import io.github.devriesl.raptormark.Constants.RUNTIME_OPT_NAME
import io.github.devriesl.raptormark.Constants.TEST_FILE_NAME_SUFFIX
import io.github.devriesl.raptormark.di.StringProvider
import org.json.JSONArray
import org.json.JSONObject

abstract class TestRepository(
    val stringProvider: StringProvider,
    val settingDataSource: SettingDataSource
) {
    abstract val testFileName: String
    open var testTypeValue: String = String()
    open var isRandTest: Boolean = false

    abstract fun getTestName(): String

    abstract fun runTest()

    fun getTestFilePath(): String {
        return settingDataSource.getAppStoragePath() + "/" + testFileName + TEST_FILE_NAME_SUFFIX
    }

    fun testOptionsBuilder(): String {
        val root = JSONObject()
        val options = JSONArray()

        root.put("shortopts", false)

        options.put(createOption(NEW_JOB_OPT_NAME, testFileName))
        options.put(createOption(FILE_PATH_OPT_NAME, getTestFilePath()))
        options.put(createOption(IO_TYPE_OPT_NAME, testTypeValue))
        options.put(createOption(IO_DEPTH_OPT_NAME, DEFAULT_IO_DEPTH_VALUE))
        options.put(createOption(RUNTIME_OPT_NAME, DEFAULT_RUNTIME_LIMIT))
        options.put(
            createOption(
                BLOCK_SIZE_OPT_NAME,
                if (isRandTest) DEFAULT_RAND_BLOCK_SIZE_VALUE else DEFAULT_SEQ_BLOCK_SIZE_VALUE
            )
        )
        options.put(createOption(IO_SIZE_OPT_NAME, DEFAULT_IO_SIZE_VALUE))
        options.put(createOption(DIRECT_IO_OPT_NAME, DIRECT_IO_CONSTANT_VALUE))
        options.put(createOption(IO_ENGINE_OPT_NAME, settingDataSource.getEngineConfig()))

        root.put("options", options)

        return root.toString()
    }

    private fun createOption(name: String, value: String): JSONObject {
        val jsonOption = JSONObject()
        jsonOption.put("name", name)
        jsonOption.put("value", value)
        return jsonOption
    }
}