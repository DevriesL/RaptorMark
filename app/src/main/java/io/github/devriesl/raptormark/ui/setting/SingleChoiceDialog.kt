package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.devriesl.raptormark.ui.widget.DialogContent

@Composable
fun SingleChoiceDialog(
    @StringRes title: Int,
    defaultChoice: String,
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
            Column {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .defaultMinSize(minHeight = 40.dp)
                        .wrapContentHeight(Alignment.Bottom)
                )
                LazyColumn(modifier = Modifier.wrapContentHeight()) {
                    items(choiceList) { choice ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .clickable { closeDialog(itemIndex, choice) }
                        ) {
                            RadioButton(
                                selected = defaultChoice == choice,
                                onClick = null,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp
                                )
                            )
                            Text(text = choice)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

        }
    }
}
