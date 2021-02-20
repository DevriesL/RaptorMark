package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.di.StringProvider

abstract class TestRepository(
    val stringProvider: StringProvider,
    val settingDataSource: SettingDataSource
) {
    abstract fun getTestName(): String
}