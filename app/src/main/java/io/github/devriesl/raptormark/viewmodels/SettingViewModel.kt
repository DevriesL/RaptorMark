package io.github.devriesl.raptormark.viewmodels

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.SettingOptions
import io.github.devriesl.raptormark.data.SettingSharedPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingSharedPrefs: SettingSharedPrefs
) : ViewModel() {
    val mutableDialogItemIndex = MutableStateFlow<Int?>(null)
    val settingItems: List<SettingItemData> = SettingOptions.values().map {
        SettingItemData(it).also { settingItemData ->
            settingItemData.data.value = it.settingData.getSettingData(settingSharedPrefs)
        }
    }

    fun openDialog(settingItemData: SettingItemData) {
        if (mutableDialogItemIndex.value == null) {
            mutableDialogItemIndex.value = settingItems.indexOf(settingItemData)
        }
    }

    fun getDialogContent(): @Composable (() -> Unit)? {
        val itemIndex = mutableDialogItemIndex.value
        return if (itemIndex != null) {
            settingItems[itemIndex].settingOptions.settingData.onDialogContent(this::closeDialog)
        } else {
            null
        }
    }

    private fun closeDialog(option: SettingOptions, result: String?) {
        val itemIndex = mutableDialogItemIndex.value
        if (result != null) {
            option.settingData.setDialogResult(settingSharedPrefs, result)
        }
        if (itemIndex != null) {
            settingItems[itemIndex].data.value =
                settingItems[itemIndex].settingOptions.settingData.getSettingData(settingSharedPrefs)
        }
        mutableDialogItemIndex.value = null
    }
}

data class SettingItemData(val settingOptions: SettingOptions) {
    val data = MutableStateFlow(String())
}
