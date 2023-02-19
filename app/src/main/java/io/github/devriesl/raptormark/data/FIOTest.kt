package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.Constants
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class FIOTest(testCase: TestCases, settingSharedPrefs: SettingSharedPrefs): BenchmarkTest(
    testCase, settingSharedPrefs
) {
    private val filePath = getRandomFilePath()

    override fun nativeTest(jsonCommand: String): Int {
        return NativeHandler.native_FIOTest(jsonCommand)
    }

    override fun runTest(): String? {
        return super.runTest().also {
            val testFile = File(filePath)
            if (testFile.exists()) testFile.delete()
        }
    }

    override fun testOptionsBuilder(): String {
        val root = JSONObject()
        val options = JSONArray()

        root.put("shortopts", false)

        options.put(createOption(Constants.NEW_JOB_OPT_NAME, testCase.name))
        options.put(createOption(Constants.FILE_PATH_OPT_NAME, filePath))
        options.put(createOption(Constants.IO_TYPE_OPT_NAME, testCase.type))
        options.put(
            createOption(
                Constants.IO_DEPTH_OPT_NAME,
                SettingOptions.IO_DEPTH.dataImpl.getValue(settingSharedPrefs)
            )
        )
        options.put(
            createOption(
                Constants.RUNTIME_OPT_NAME,
                SettingOptions.RUNTIME_LIMIT.dataImpl.getValue(settingSharedPrefs)
            )
        )
        options.put(
            createOption(
                Constants.BLOCK_SIZE_OPT_NAME,
                if (testCase.isRand) {
                    SettingOptions.RAND_BLOCK_SIZE.dataImpl.getValue(settingSharedPrefs)
                } else {
                    SettingOptions.SEQ_BLOCK_SIZE.dataImpl.getValue(settingSharedPrefs)
                }
            )
        )
        options.put(
            createOption(
                Constants.IO_SIZE_OPT_NAME,
                SettingOptions.IO_SIZE.dataImpl.getValue(settingSharedPrefs)
            )
        )
        options.put(createOption(Constants.DIRECT_IO_OPT_NAME, Constants.CONSTANT_DIRECT_IO_VALUE))
        options.put(
            createOption(
                Constants.IO_ENGINE_OPT_NAME,
                SettingOptions.IO_ENGINE.dataImpl.getValue(settingSharedPrefs)
            )
        )
        options.put(
            createOption(
                Constants.NUM_THREADS_OPT_NAME,
                SettingOptions.NUM_THREADS.dataImpl.getValue(settingSharedPrefs)
            )
        )

        options.put(createOption(Constants.ETA_PRINT_OPT_NAME, Constants.CONSTANT_ETA_PRINT_VALUE))
        options.put(createOption(
            Constants.OUTPUT_FORMAT_OPT_NAME,
            Constants.CONSTANT_OUTPUT_FORMAT_VALUE
        ))

        root.put("options", options)

        return root.toString()
    }

    private fun getRandomFilePath(): String {
        val randomSuffix = List(FILE_SUFFIX_LENGTH) {
            (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
        }.joinToString("")

        return settingSharedPrefs.getTestDirPath() + "/" + testCase.name + randomSuffix
    }

    companion object {
        const val FILE_SUFFIX_LENGTH = 8
    }
}