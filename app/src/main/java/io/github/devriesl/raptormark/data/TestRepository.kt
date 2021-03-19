package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.Constants.FILE_PATH_OPT_NAME
import io.github.devriesl.raptormark.Constants.IO_ENGINE_OPT_NAME
import io.github.devriesl.raptormark.Constants.TEST_FILE_NAME_SUFFIX
import io.github.devriesl.raptormark.di.StringProvider
import org.json.JSONArray
import org.json.JSONObject

abstract class TestRepository(
    val stringProvider: StringProvider,
    val settingDataSource: SettingDataSource
) {
    abstract val testFileName: String

    abstract fun getTestName(): String

    abstract fun runTest()

    fun getTestFilePath(): String {
        return settingDataSource.getAppStoragePath() + "/" + testFileName + TEST_FILE_NAME_SUFFIX
    }

    fun testOptionsBuilder(): String {
        val root = JSONObject()
        val options = JSONArray()

        root.put("shortopts", false)

        options.put(createOption(FILE_PATH_OPT_NAME, getTestFilePath()))
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