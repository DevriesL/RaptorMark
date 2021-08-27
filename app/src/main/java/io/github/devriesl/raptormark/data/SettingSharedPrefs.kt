package io.github.devriesl.raptormark.data

import android.content.Context

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

    fun getDefaultDirPath(): String {
        return appFilesDir.absolutePath
    }

    fun getTestDirPath(): String {
        return appFilesDir.absolutePath
    }

    fun setTestDirPath(path: String) {
        with(sharedPrefs.edit()) {
            putString(TEST_DIR_PATH_KEY, path)
            commit()
        }
    }

    companion object {
        const val SHARED_PREFS_NAME = "raptor_mark_settings"
        const val TEST_DIR_PATH_KEY = "test_dir_path"

        @Volatile
        private var INSTANCE: SettingSharedPrefs? = null

        fun getInstance(context: Context): SettingSharedPrefs =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingSharedPrefs(context).also { INSTANCE = it }
            }
    }
}