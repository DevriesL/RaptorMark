package io.github.devriesl.raptormark.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource

@Composable
fun SettingItem(
    @StringRes title: Int,
    @StringRes desc: Int,
    data: String,
    isCard: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    openDialog: () -> Unit
) {
    val shape = when {
        !isCard -> RectangleShape
        isFirst -> MaterialTheme.shapes.medium.copy(
            bottomEnd = CornerSize(0),
            bottomStart = CornerSize(0)
        )
        isLast -> MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(0),
            topEnd = CornerSize(0)
        )
        else -> RectangleShape
    }
    val colors = if (isCard) {
        ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        ListItemDefaults.colors()
    }
    ListItem(
        colors = colors,
        headlineContent = {
            Text(text = stringResource(id = title))
        },
        supportingContent = {
            Text(text = stringResource(id = desc))
        },
        trailingContent = {
            Text(text = data)
        },
        modifier = Modifier
            .clip(shape)
            .clickable(onClick = openDialog)
    )
}