package io.github.devriesl.raptormark.ui.setting

import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        elevation = 16.dp,
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
        content = content
    )
}