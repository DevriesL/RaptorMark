package io.github.devriesl.raptormark.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.SettingOptions
import io.github.devriesl.raptormark.data.SettingSharedPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingSharedPrefs: SettingSharedPrefs
) : ViewModel() {
    val settingItems: List<SettingItemData> =
        SettingOptions.values().map { SettingItemData(it) }

    fun openDialog(settingOptions: SettingOptions) {
    }
}

data class SettingItemData(val settingOptions: SettingOptions) {
    val mutableItemData = MutableStateFlow(String())

    val itemData: StateFlow<String>
        get() = mutableItemData
}

