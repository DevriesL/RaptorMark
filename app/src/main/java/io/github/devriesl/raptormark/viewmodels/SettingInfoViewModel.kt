package io.github.devriesl.raptormark.viewmodels

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.devriesl.raptormark.data.InfoRepository
import io.github.devriesl.raptormark.dialogs.IDialogResultReceiver

class SettingInfoViewModel(private val infoRepo: InfoRepository) : ViewModel() {
    val infoTitle = infoRepo.getInfoTitle()
    val infoData = MutableLiveData<String>()
    val infoDesc = infoRepo.getInfoDesc()
    private val dialogResultReceiver = object : IDialogResultReceiver {
        override fun onResultReceived(result: Any) {
            infoRepo.setDialogResult(result)
            infoData.postValue(infoRepo.getInfoData())
        }
    }
    private val dialog = infoRepo.registerDialog(dialogResultReceiver)

    init {
        infoData.postValue(infoRepo.getInfoData())
    }

    fun showDialog(fragmentManager: FragmentManager) {
        val dialogTag = infoTitle + DIALOG_TAG_SUFFIX
        dialog?.show(fragmentManager, dialogTag)
    }

    companion object {
        const val DIALOG_TAG_SUFFIX = "Dialog"
    }
}
