package io.github.devriesl.raptormark.data

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import io.github.devriesl.raptormark.Constants.DEFAULT_IO_ENGINE_VALUE
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.ui.setting.SingleChoiceDialog
import org.json.JSONObject

enum class SettingOptions(
    @StringRes val title: Int,
    @StringRes val desc: Int,
    val settingData: ISettingData
) {
    ENGINE_CONFIG(R.string.engine_config_title, R.string.engine_config_desc, object : ISettingData {
        override fun getSettingData(settingSharedPrefs: SettingSharedPrefs): String {
            return settingSharedPrefs.getConfig(ENGINE_CONFIG.name, DEFAULT_IO_ENGINE_VALUE)
        }

        override fun onDialogContent(
            itemIndex: Int,
            closeDialog: (Int, String?) -> Unit
        ): @Composable () -> Unit {
            val engineList: ArrayList<String> = ArrayList()
            val jsonObject = JSONObject(NativeDataSource.native_ListEngines())
            val jsonArray = jsonObject.getJSONArray("engines")
            for (i in 0 until jsonArray.length()) {
                val engineObject: JSONObject = jsonArray.getJSONObject(i)
                val engineItem = engineObject.getString("name")
                val engineAvailable = engineObject.getBoolean("available")
                if (engineAvailable) {
                    engineList.add(engineItem)
                }
            }

            return {
                SingleChoiceDialog(
                    title = ENGINE_CONFIG.title,
                    choiceList = engineList,
                    itemIndex = itemIndex,
                    closeDialog = closeDialog
                )
            }
        }

        override fun setDialogResult(settingSharedPrefs: SettingSharedPrefs, result: String) {
            settingSharedPrefs.setConfig(ENGINE_CONFIG.name, result)
        }
    })
}
