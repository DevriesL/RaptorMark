package io.github.devriesl.raptormark.ui.widget

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

@Composable
fun rememberHideOnScrollState(isShow: Boolean = true): HideOnScrollState {
    return remember(isShow) {
        HideOnScrollState(isShow)
    }
}

@Stable
class HideOnScrollState(isShow: Boolean = true) {

    val isShow = MutableTransitionState(isShow)

    fun hide() {
        isShow.targetState = false
    }

    fun show() {
        isShow.targetState = true
    }

    override fun hashCode(): Int {
        return isShow.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is HideOnScrollState)  return false
        return isShow == other.isShow
    }
}

class HideOnScrollNestedScrollConnection(
    private val hideOnScrollState: HideOnScrollState,
    private val canHide: () -> Boolean = { true }
) : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (canHide()) {
            if (consumed.y > 0) {
                hideOnScrollState.show()
            } else if (consumed.y < 0) {
                hideOnScrollState.hide()
            }
        }
        return super.onPostScroll(consumed, available, source)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (canHide()) {
            if (consumed.y > 0) {
                hideOnScrollState.show()
            } else if (consumed.y < 0) {
                hideOnScrollState.hide()
            }
        }
        return super.onPostFling(consumed, available)

    }
}
