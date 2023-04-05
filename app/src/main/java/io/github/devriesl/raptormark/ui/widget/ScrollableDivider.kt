package io.github.devriesl.raptormark.ui.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun ScrollableDivider(
    state: LazyListState,
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness
) {
    val isListOnTop by remember {
        derivedStateOf { state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0 }
    }
    val targetThickness by animateDpAsState(
        targetValue = if (isListOnTop) {
            Dp.Unspecified
        } else {
            thickness
        }
    )
    Divider(
        thickness = targetThickness,
        modifier = Modifier.then(modifier)
    )
}