package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.Constants.TEST_FILE_NAME_SUFFIX
import io.github.devriesl.raptormark.di.StringProvider

abstract class TestRepository(
    val stringProvider: StringProvider,
    val settingDataSource: SettingDataSource
) {
    abstract val testFileName: String

    abstract fun getTestName(): String

    fun getTestFilePath(): String {
        return settingDataSource.getAppStoragePath() + "/" + testFileName + TEST_FILE_NAME_SUFFIX
    }
}