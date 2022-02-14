package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.devriesl.raptormark.R
import kotlinx.coroutines.delay

internal const val showSoftKeyboardDelayTime = 300L

@Composable
fun TextInputDialog(
    @StringRes title: Int,
    defaultValue: String,
    currentValue: String,
    keyboardType: KeyboardType,
    itemIndex: Int,
    closeDialog: (Int, String?) -> Unit,
) {
    Dialog(onDismissRequest = { closeDialog(itemIndex, null) }) {
        var textFieldValue by remember {
            mutableStateOf(
                TextFieldValue(
                    text = currentValue,
                    selection = TextRange(0, currentValue.length),
                    composition = TextRange(0, currentValue.length)
                )
            )
        }

        val focusRequester = remember { FocusRequester() }
        val inputService = LocalTextInputService.current
        LaunchedEffect(Unit) {
            //Wait a little time to make sure the input service wakes up the keyboard
            delay(showSoftKeyboardDelayTime)
            inputService?.showSoftwareKeyboard()
            focusRequester.requestFocus()
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
                        .padding(bottom = 16.dp)
                        .defaultMinSize(minHeight = 40.dp)
                        .wrapContentHeight(Alignment.Bottom)
                )
                OutlinedTextField(
                    value = textFieldValue,
                    placeholder = { Text(text = defaultValue) },
                    onValueChange = { textFieldValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp
                        )
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    TextButton(
                        onClick = { closeDialog(itemIndex, null) },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(stringResource(R.string.dismiss_button_content))
                    }
                    TextButton(
                        onClick = {
                            if (textFieldValue.text.isEmpty()) {
                                closeDialog(itemIndex, defaultValue)
                            } else {
                                closeDialog(itemIndex, textFieldValue.text)
                            }
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.apply_button_content))
                    }
                }
            }
        }
    }
}
