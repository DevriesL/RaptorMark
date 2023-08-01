package io.github.devriesl.raptormark.viewmodels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.SettingOptions
import io.github.devriesl.raptormark.data.SettingSharedPrefs
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingSharedPrefs: SettingSharedPrefs
) : ViewModel() {
    var dialogContent by mutableStateOf<@Composable (() -> Unit)?>(null)
        private set

    val settingItems: List<SettingItemData> = SettingOptions.values().map {
        SettingItemData(it).also { settingItemData ->
            settingItemData.data.value = it.dataImpl.getValue(settingSharedPrefs)
        }
    }

    fun openDialog(settingItemData: SettingItemData) {
        val itemIndex = settingItems.indexOf(settingItemData)
        if (dialogContent == null) {
            dialogContent = settingItemData.option.dataImpl.onDialogContent(
                settingSharedPrefs,
                itemIndex,
                this::closeDialog
            )
        }
    }

    private fun closeDialog(itemIndex: Int, result: String?) {
        if (result != null) {
            settingItems[itemIndex].option.dataImpl.setDialogResult(
                settingSharedPrefs,
                result
            )
        }
        settingItems[itemIndex].data.value =
            settingItems[itemIndex].option.dataImpl.getValue(settingSharedPrefs)
        dialogContent = null
    }
}

data class SettingItemData(val option: SettingOptions) {
    val data = mutableStateOf(String())
}
