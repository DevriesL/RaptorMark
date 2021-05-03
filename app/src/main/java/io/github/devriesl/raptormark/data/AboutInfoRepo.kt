package io.github.devriesl.raptormark.data

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.github.devriesl.raptormark.BuildConfig
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider
import io.github.devriesl.raptormark.dialogs.AboutDialog
import io.github.devriesl.raptormark.dialogs.IDialogResultReceiver

class AboutInfoRepo(
    stringProvider: StringProvider,
    settingDataSource: SettingDataSource
) : InfoRepository(stringProvider, settingDataSource) {
    override fun getInfoTitle(): String {
        return stringProvider.getString(R.string.about_title)
    }

    override fun getInfoData(): String {
        return stringProvider.getString(R.string.app_version)
    }

    override fun getInfoDesc(): String {
        return stringProvider.getString(R.string.about_desc)
    }

    override fun registerDialog(receiver: IDialogResultReceiver): DialogFragment {
        return AboutDialog(getInfoTitle())
    }
}
