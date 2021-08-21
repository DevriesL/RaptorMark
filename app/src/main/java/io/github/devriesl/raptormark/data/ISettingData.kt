package io.github.devriesl.raptormark.data

import androidx.compose.runtime.Composable

interface ISettingData {
    fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String
    fun onDialogContent(): @Composable () -> Unit
    fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: Any)
}