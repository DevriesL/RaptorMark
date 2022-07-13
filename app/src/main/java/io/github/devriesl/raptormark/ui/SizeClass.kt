package io.github.devriesl.raptormark.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class SizeClass {
    Compact,
    Medium,
    Expanded;

    companion object {
        fun from(size: Dp) = when {
            size < 600.dp -> Compact
            size < 840.dp -> Medium
            else -> Expanded
        }
    }
}

@Composable
fun rememberSizeClass(size: Dp) = remember(size) {
    SizeClass.from(size)
}