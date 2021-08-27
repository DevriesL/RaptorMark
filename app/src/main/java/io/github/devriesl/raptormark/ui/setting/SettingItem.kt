package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingItem(
    @StringRes title: Int,
    @StringRes desc: Int,
    data: String,
    openDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = openDialog)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Row {
            Text(
                text = stringResource(title),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = data,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
        Text(
            text = stringResource(desc),
            fontSize = 14.sp
        )
    }
}