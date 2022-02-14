package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.devriesl.raptormark.R
import kotlinx.coroutines.delay

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
        val inputService = LocalTextInputService.current
        LaunchedEffect(key1 = selectCustomPath.value) {
            if (selectCustomPath.value) {
                delay(300)
                inputService?.showSoftwareKeyboard()
                focusRequester.requestFocus()
            }else {
                inputService?.hideSoftwareKeyboard()
            }
        }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .fillMaxWidth()
                        .clickable { selectCustomPath.value = false }
                ) {
                    RadioButton(
                        selected = !selectCustomPath.value,
                        onClick = null,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(stringResource(R.string.target_path_default))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectCustomPath.value,
                        onClick = { selectCustomPath.value = true },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    OutlinedTextField(
                        value = textFieldValue,
                        placeholder = { Text(stringResource(R.string.target_path_custom)) },
                        onValueChange = { textFieldValue = it },
                        enabled = selectCustomPath.value,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
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
            }
        }
    }
}
