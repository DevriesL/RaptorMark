package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SingleChoiceDialog(
    @StringRes title: Int,
    choiceList: ArrayList<String>,
    itemIndex: Int,
    closeDialog: (Int, String?) -> Unit,
) {
    Dialog(onDismissRequest = { closeDialog(itemIndex, null) }) {
        DialogContent(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(title))
                Divider(Modifier.padding(top = 8.dp))
                LazyColumn(modifier = Modifier.wrapContentHeight()) {
                    items(choiceList) { choice ->
                        Text(text = choice, Modifier
                            .clickable { closeDialog(itemIndex, choice) }
                            .fillMaxWidth()
                            .padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}
