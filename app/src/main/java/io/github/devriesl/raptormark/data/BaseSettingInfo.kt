package io.github.devriesl.raptormark.data

abstract class BaseSettingInfo {
    abstract fun getSettingTitle(): String
    abstract fun getSettingData(): String
    abstract fun getSettingDesc(): String
}
