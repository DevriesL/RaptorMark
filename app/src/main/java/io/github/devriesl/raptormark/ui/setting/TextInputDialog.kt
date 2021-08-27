package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.devriesl.raptormark.R

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
        var textFieldValue by remember { mutableStateOf(TextFieldValue(currentValue)) }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(title))
                Divider(Modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = textFieldValue,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    placeholder = { Text(text = defaultValue) },
                    onValueChange = { textFieldValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
                )
                Divider(Modifier.padding(vertical = 8.dp))
                Row {
                    Button(
                        onClick = { closeDialog(itemIndex, null) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(stringResource(R.string.dismiss_button_content))
                    }
                    Button(
                        onClick = {
                            if (textFieldValue.text.isEmpty()) {
                                closeDialog(itemIndex, defaultValue)
                            } else {
                                closeDialog(itemIndex, textFieldValue.text)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(stringResource(R.string.apply_button_content))
                    }
                }
            }
        }
    }
}
