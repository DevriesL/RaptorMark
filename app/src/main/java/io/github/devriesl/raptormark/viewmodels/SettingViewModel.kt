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
    val dialogContent = MutableStateFlow<@Composable (() -> Unit)?>(null)
    val settingItems: List<SettingItemData> = SettingOptions.values().map {
        SettingItemData(it).also { settingItemData ->
            settingItemData.data.value = it.dataImpl.getValue(settingSharedPrefs)
        }
    }

    fun openDialog(settingItemData: SettingItemData) {
        val itemIndex = settingItems.indexOf(settingItemData)
        if (dialogContent.value == null) {
            dialogContent.value = settingItemData.option.dataImpl.onDialogContent(
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
        dialogContent.value = null
    }
}

data class SettingItemData(val option: SettingOptions) {
    val data = MutableStateFlow(String())
}
