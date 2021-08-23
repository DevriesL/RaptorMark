package io.github.devriesl.raptormark.data

import androidx.compose.runtime.Composable

interface ISettingData {
    fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String
    fun onDialogContent(itemIndex: Int, closeDialog: (Int, String?) -> Unit): @Composable () -> Unit
    fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String)
}