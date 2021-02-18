package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.di.StringProvider

abstract class InfoRepository(
    val stringProvider: StringProvider,
    val settingDataSource: SettingDataSource
) {
    abstract fun getInfoTitle(): String
    abstract fun getInfoData(): String
    abstract fun getInfoDesc(): String
}
