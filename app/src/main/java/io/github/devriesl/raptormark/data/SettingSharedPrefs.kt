package io.github.devriesl.raptormark.data

import android.content.Context
import org.json.JSONObject

class SettingSharedPrefs private constructor(context: Context) {
    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    private val appFilesDir = context.filesDir

    fun getConfig(key: String, defValue: String): String {
        val value = sharedPrefs.getString(key, defValue)
        return value ?: defValue
    }

    fun setConfig(key: String, value: String) {
        with(sharedPrefs.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun getEngineList(): ArrayList<String> {
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
        return engineList
    }

    fun getAppStoragePath(): String {
        return appFilesDir.absolutePath
    }

    companion object {
        const val SHARED_PREFS_NAME = "raptor_mark_settings"

        @Volatile
        private var INSTANCE: SettingSharedPrefs? = null

        fun getInstance(context: Context): SettingSharedPrefs =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingSharedPrefs(context).also { INSTANCE = it }
            }
    }
}