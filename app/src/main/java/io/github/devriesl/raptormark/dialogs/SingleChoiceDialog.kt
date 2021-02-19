package io.github.devriesl.raptormark.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SingleChoiceDialog(
    private val title: String,
    val values: ArrayList<String>,
    private val receiver: IDialogResultReceiver
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
            builder.setItems(values.toTypedArray()) { _, which -> receiver.onResultReceived(values[which]) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
