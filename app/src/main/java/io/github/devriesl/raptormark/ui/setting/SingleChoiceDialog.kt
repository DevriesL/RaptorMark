package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.devriesl.raptormark.ui.widget.*

@Composable
fun SingleChoiceDialog(
    @StringRes title: Int,
    defaultChoice: String,
    choiceList: ArrayList<String>,
    itemIndex: Int,
    closeDialog: (Int, String?) -> Unit,
) {
    Dialog(
        onDismissRequest = { closeDialog(itemIndex, null) },
    ) {
        DialogContent(
            title = {
                Text(text = stringResource(id = title))
            }
        ) {
            Box {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    contentPadding = DialogContentDefaults.CONTENT_PADDING
                ) {
                    items(choiceList) { choice ->
                        ListItem(
                            headlineContent = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = defaultChoice == choice,
                                        onClick = null,
                                        modifier = Modifier.padding(
                                            start = 8.dp,
                                            end = 16.dp
                                        )
                                    )
                                    Text(text = choice)
                                }
                            },
                            modifier = Modifier.clickable {
                                closeDialog(itemIndex, choice)
                            }
                        )
                    }
                }
                ScrollableDivider(state = listState)
            }
        }
    }
}
