package io.github.devriesl.raptormark.viewmodels

import androidx.lifecycle.ViewModel
import io.github.devriesl.raptormark.data.InfoRepository

class SettingInfoViewModel(infoRepo: InfoRepository) : ViewModel() {

    val infoTitle = infoRepo.getInfoTitle()
    val infoData = infoRepo.getInfoData()
    val infoDesc = infoRepo.getInfoDesc()
}
