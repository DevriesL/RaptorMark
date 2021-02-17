package io.github.devriesl.raptormark.data

interface SettingDataSource {
    fun getEngineConfig(default: String): String
    fun setEngineConfig(engine: String)
}
