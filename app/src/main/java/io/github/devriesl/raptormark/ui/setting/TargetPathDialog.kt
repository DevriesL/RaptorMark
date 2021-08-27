package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.devriesl.raptormark.R

@Composable
fun TargetPathDialog(
    @StringRes title: Int,
    customValue: String,
    itemIndex: Int,
    closeDialog: (Int, String?) -> Unit,
) {
    Dialog(onDismissRequest = { closeDialog(itemIndex, null) }) {
        var textFieldValue by remember { mutableStateOf(TextFieldValue(customValue)) }
        val selectCustomPath = mutableStateOf(customValue.isNotEmpty())

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(title))
                Divider(Modifier.padding(vertical = 8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !selectCustomPath.value,
                        onClick = { selectCustomPath.value = false })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.target_path_default))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectCustomPath.value,
                        onClick = { selectCustomPath.value = true })
                    OutlinedTextField(
                        value = textFieldValue,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.target_path_custom)) },
                        onValueChange = { textFieldValue = it },
                        enabled = selectCustomPath.value
                    )
                }
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
                            if (selectCustomPath.value) {
                                closeDialog(itemIndex, textFieldValue.text)
                            } else {
                                closeDialog(itemIndex, String())
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
