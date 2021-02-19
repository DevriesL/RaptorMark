package io.github.devriesl.raptormark.data

import androidx.fragment.app.DialogFragment
import io.github.devriesl.raptormark.di.StringProvider
import io.github.devriesl.raptormark.dialogs.IDialogResultReceiver

abstract class InfoRepository(
    val stringProvider: StringProvider,
    val settingDataSource: SettingDataSource
) {
    abstract fun getInfoTitle(): String
    abstract fun getInfoData(): String
    abstract fun getInfoDesc(): String

    open fun registerDialog(receiver: IDialogResultReceiver): DialogFragment? = null
    open fun setDialogResult(result: Any) {}
}
