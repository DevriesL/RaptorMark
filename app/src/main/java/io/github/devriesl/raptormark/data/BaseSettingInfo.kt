package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.di.StringProvider

abstract class BaseSettingInfo(
    val stringProvider: StringProvider,
    val settingDataSource: SettingDataSource
) {
    abstract fun getSettingTitle(): String
    abstract fun getSettingData(): String
    abstract fun getSettingDesc(): String
}
