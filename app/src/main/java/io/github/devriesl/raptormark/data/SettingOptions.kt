package io.github.devriesl.raptormark.data

import androidx.annotation.StringRes
import io.github.devriesl.raptormark.Constants.DEFAULT_IO_ENGINE_VALUE
import io.github.devriesl.raptormark.R

enum class SettingOptions(
    @StringRes val title: Int,
    @StringRes val desc: Int,
    val settingData: ISettingData
) {
    ENGINE_CONFIG(R.string.engine_config_title, R.string.engine_config_desc, object : ISettingData {
        override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
            return settingSharedPrefs.getConfig(ENGINE_CONFIG.name, DEFAULT_IO_ENGINE_VALUE)
        }

        override fun onDialogContent(): () -> Unit {
            TODO("Not yet implemented")
        }

        override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: Any) {
            settingSharedPrefs.setConfig(ENGINE_CONFIG.name, result as String)
        }
    })
}
