package io.github.devriesl.raptormark.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.github.devriesl.raptormark.R

class AboutDialog(private val title: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
            builder.setView(R.layout.dialog_about)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
