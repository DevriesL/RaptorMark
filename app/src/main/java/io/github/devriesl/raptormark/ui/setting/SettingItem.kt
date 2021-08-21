package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SettingItem(
    @StringRes title: Int,
    @StringRes desc: Int,
    data: String,
    openDialog: () -> Unit
) {
    Column(modifier = Modifier.clickable(onClick = openDialog)) {
        Row {
            Text(text = stringResource(title))
            Text(
                text = data,
                textAlign = TextAlign.End
            )
        }
        Text(text = stringResource(desc))
    }
}