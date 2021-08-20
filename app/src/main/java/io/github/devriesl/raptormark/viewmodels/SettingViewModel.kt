package io.github.devriesl.raptormark.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.SettingDataSource
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingDataSource: SettingDataSource
) : ViewModel() {
}
