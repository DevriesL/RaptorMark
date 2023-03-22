package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.ui.widget.DialogContent
import io.github.devriesl.raptormark.ui.widget.DialogContentDefaults
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TargetPathDialog(
    @StringRes title: Int,
    customValue: String,
    itemIndex: Int,
    closeDialog: (Int, String?) -> Unit,
) {
    Dialog(onDismissRequest = { closeDialog(itemIndex, null) }) {
        var textFieldValue by remember { mutableStateOf(TextFieldValue(customValue)) }
        val selectCustomPath = remember { mutableStateOf(customValue.isNotEmpty()) }
        val focusRequester = remember { FocusRequester() }
        val softwareKeyboardController = LocalSoftwareKeyboardController.current
        LaunchedEffect(key1 = selectCustomPath.value) {
            if (selectCustomPath.value) {
                delay(TextInputDialogDefault.SHOW_SOFT_KEYBOARD_DELAY_TIME)
                softwareKeyboardController?.show()
                focusRequester.requestFocus()
            } else {
                softwareKeyboardController?.hide()
            }
        }
        DialogContent(
            title = {
                Text(text = stringResource(title))
            },
            buttons = {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = DialogContentDefaults.DIALOG_PADDING.calculateBottomPadding())
                ) {
                    TextButton(
                        onClick = { closeDialog(itemIndex, null) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.dismiss_button_content))
                    }
                    TextButton(
                        onClick = {
                            if (selectCustomPath.value) {
                                closeDialog(itemIndex, textFieldValue.text)
                            } else {
                                closeDialog(itemIndex, String())
                            }
                        }
                    ) {
                        Text(stringResource(R.string.apply_button_content))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(DialogContentDefaults.CONTENT_PADDING)) {
                ListItem(
                    headlineText = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = !selectCustomPath.value,
                                onClick = null,
                                modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                            )
                            Text(
                                text = stringResource(R.string.target_path_default)
                            )
                        }
                    },
                    modifier = Modifier.clickable {
                        selectCustomPath.value = false
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectCustomPath.value,
                        onClick = { selectCustomPath.value = true },
                        modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                    )
                    OutlinedTextField(
                        value = textFieldValue,
                        placeholder = { Text(stringResource(R.string.target_path_custom)) },
                        onValueChange = { textFieldValue = it },
                        enabled = selectCustomPath.value,
                        modifier = Modifier
                            .padding(
                                end = DialogContentDefaults.DIALOG_PADDING.calculateEndPadding(
                                    LocalLayoutDirection.current
                                )
                            )
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    )
                }

            }
        }
    }
}
