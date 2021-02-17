package io.github.devriesl.raptormark.viewmodels

import androidx.lifecycle.ViewModel
import io.github.devriesl.raptormark.data.BaseSettingInfo
import io.github.devriesl.raptormark.data.SettingInfoRepository

class SettingInfoViewModel(settingInfo: BaseSettingInfo) : ViewModel() {
    private val settingInfoRepository: SettingInfoRepository = SettingInfoRepository(settingInfo)

    val infoTitle = settingInfoRepository.getTitle()
    val infoData = settingInfoRepository.getData()
    val infoDesc = settingInfoRepository.getDesc()
}
