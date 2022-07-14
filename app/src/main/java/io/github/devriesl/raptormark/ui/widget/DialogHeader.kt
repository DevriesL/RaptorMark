package io.github.devriesl.raptormark.ui.widget

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun DialogHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.h6,
        modifier = Modifier
            .height(64.dp)
            .paddingFromBaseline(40.dp)
            .then(modifier)
    )
}

object DialogHeaderDefaults {
    val HEADER_HORIZONTAL_PADDING = 16.dp
}