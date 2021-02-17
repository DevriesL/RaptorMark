package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider

class EngineSettingInfo(
    stringProvider: StringProvider,
    settingDataSource: SettingDataSource
) : BaseSettingInfo(stringProvider, settingDataSource) {
    override fun getSettingTitle(): String {
        return stringProvider.getString(R.string.engine_config_title)
    }

    override fun getSettingData(): String {
        TODO("Not yet implemented")
    }

    override fun getSettingDesc(): String {
        return stringProvider.getString(R.string.engine_config_desc)
    }
}
