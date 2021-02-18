package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider
import org.json.JSONObject

class EngineInfoRepo(
    stringProvider: StringProvider,
    settingDataSource: SettingDataSource
) : InfoRepository(stringProvider, settingDataSource) {

    private var engineList: ArrayList<String> = ArrayList()
    private var engineConfig: String

    init {
        getEngineList()
        engineConfig = settingDataSource.getEngineConfig(getPreferredEngine())
    }

    private fun getEngineList() {
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
    }

    private fun getPreferredEngine(): String {
        return if (engineList.contains(PRIMARY_PREFERRED_ENGINE)) {
            PRIMARY_PREFERRED_ENGINE
        } else {
            SECONDARY_PREFERRED_ENGINE
        }
    }

    override fun getInfoTitle(): String {
        return stringProvider.getString(R.string.engine_config_title)
    }

    override fun getInfoData(): String {
        return engineConfig
    }

    override fun getInfoDesc(): String {
        return stringProvider.getString(R.string.engine_config_desc)
    }

    companion object {
        const val PRIMARY_PREFERRED_ENGINE = "io_uring"
        const val SECONDARY_PREFERRED_ENGINE = "psync"
    }
}
