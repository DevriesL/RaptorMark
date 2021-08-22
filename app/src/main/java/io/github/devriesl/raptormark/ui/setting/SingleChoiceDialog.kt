package io.github.devriesl.raptormark.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.devriesl.raptormark.data.SettingOptions

@Composable
fun SingleChoiceDialog(
    option: SettingOptions,
    choiceList: ArrayList<String>,
    closeDialog: (SettingOptions, String?) -> Unit,
) {
    Dialog(onDismissRequest = { closeDialog(option, null) }) {
        Column {
            Text(text = stringResource(option.title))

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(choiceList) { choice ->
                    Text(text = choice, Modifier.clickable { closeDialog(option, choice) })
                }
            }
        }
    }
}
