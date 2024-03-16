package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import io.github.devriesl.raptormark.R
import kotlinx.coroutines.delay

internal object TextInputDialogDefault {
    const val SHOW_SOFT_KEYBOARD_DELAY_TIME = 300L
}

@Composable
fun TextInputDialog(
    @StringRes title: Int,
    defaultValue: String,
    currentValue: String,
    keyboardType: KeyboardType,
    itemIndex: Int,
    closeDialog: (Int, String?) -> Unit,
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentValue,
                selection = TextRange(0, currentValue.length),
                composition = TextRange(0, currentValue.length)
            )
        )
    }
    AlertDialog(
        onDismissRequest = { closeDialog(itemIndex, null) },
        title = {
            Text(text = stringResource(id = title))
        },
        text = {

            val focusRequester = remember { FocusRequester() }
            val softwareKeyboardController = LocalSoftwareKeyboardController.current
            LaunchedEffect(Unit) {
                //Wait a little time to make sure the input service wakes up the keyboard
                delay(TextInputDialogDefault.SHOW_SOFT_KEYBOARD_DELAY_TIME)
                softwareKeyboardController?.show()
                focusRequester.requestFocus()
            }

            OutlinedTextField(
                value = textFieldValue,
                placeholder = { Text(text = defaultValue) },
                onValueChange = { textFieldValue = it },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (textFieldValue.text.isEmpty()) {
                        closeDialog(itemIndex, defaultValue)
                    } else {
                        closeDialog(itemIndex, textFieldValue.text)
                    }
                }
            ) {
                Text(stringResource(R.string.apply_button_content))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { closeDialog(itemIndex, null) },
                modifier = Modifier
            ) {
                Text(stringResource(R.string.dismiss_button_content))
            }
        },
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    )
}
