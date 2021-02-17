package io.github.devriesl.raptormark.data

const val ENGINE_CONFIG_SETTING_ID = "engine_config_setting"

object SettingItems {
    var settingList: List<SettingItem> = listOf(
        SettingItem(ENGINE_CONFIG_SETTING_ID, EngineSettingInfo()),
    )
}

data class SettingItem(
    var id: String = "",
    var settingInfo: BaseSettingInfo
)
