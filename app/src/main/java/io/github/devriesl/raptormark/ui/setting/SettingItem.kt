package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
    Box(
        modifier = Modifier
            .clickable(onClick = openDialog)
            .padding(horizontal = 16.dp)
            .defaultMinSize(minHeight = 64.dp)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.paddingFromBaseline(28.dp)) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(28.dp))
            Text(
                text = data,
                color = LocalContentColor.current.copy(ContentAlpha.medium),
                textAlign = TextAlign.End,
                modifier = Modifier.defaultMinSize(40.dp),
            )
        }
        Text(
            text = stringResource(desc),
            style = MaterialTheme.typography.body2,
            color = LocalContentColor.current.copy(ContentAlpha.medium),
            modifier = Modifier.paddingFromBaseline(48.dp)
        )
    }
}