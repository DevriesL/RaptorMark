package io.github.devriesl.raptormark.data

import androidx.fragment.app.DialogFragment
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider
import io.github.devriesl.raptormark.dialogs.IDialogResultReceiver
import io.github.devriesl.raptormark.dialogs.SingleChoiceDialog

class EngineInfoRepo(
    stringProvider: StringProvider,
    settingDataSource: SettingDataSource
) : InfoRepository(stringProvider, settingDataSource) {

    override fun getInfoTitle(): String {
        return stringProvider.getString(R.string.engine_config_title)
    }

    override fun getInfoData(): String {
        return settingDataSource.getEngineConfig()
    }

    override fun getInfoDesc(): String {
        return stringProvider.getString(R.string.engine_config_desc)
    }

    override fun registerDialog(receiver: IDialogResultReceiver): DialogFragment {
        return SingleChoiceDialog(getInfoTitle(), settingDataSource.getEngineList(), receiver)
    }

    override fun setDialogResult(result: Any) {
        settingDataSource.setEngineConfig(result as String)
    }
}
