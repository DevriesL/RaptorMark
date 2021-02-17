package io.github.devriesl.raptormark.data

class SettingInfoRepository(private val settingInfo: BaseSettingInfo) {
    fun getTitle() = settingInfo.getSettingTitle()

    fun getData() = settingInfo.getSettingData()

    fun getDesc() = settingInfo.getSettingDesc()
}
