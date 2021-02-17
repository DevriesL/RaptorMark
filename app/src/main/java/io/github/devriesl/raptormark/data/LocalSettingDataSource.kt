package io.github.devriesl.raptormark.data

import android.content.Context

class LocalSettingDataSource(context: Context) : SettingDataSource {
    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override fun getEngineConfig(default: String): String {
        val engine = sharedPrefs.getString(ENGINE_CONFIG_KEY, default)
        return engine ?: default
    }

    override fun setEngineConfig(engine: String) {
        with(sharedPrefs.edit()) {
            putString(ENGINE_CONFIG_KEY, engine)
            commit()
        }
    }

    companion object {
        const val SHARED_PREFS_NAME = "raptor_mark_settings"
        const val ENGINE_CONFIG_KEY = "engine_config"
    }
}
