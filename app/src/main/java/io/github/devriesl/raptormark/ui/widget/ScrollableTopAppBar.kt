/**
 * fork from https://github.com/androidx/androidx/blob/androidx-main/compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/AppBar.kt
 *  modify some source code
 * this file will be remove when RaptorMark migrate to Material 3.
 */

package io.github.devriesl.raptormark.ui.widget

import androidx.compose.animation.core.*
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max

@Composable
fun ScrollableTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    draggable: Boolean = true,
    content: @Composable () -> Unit
) {
    val heightOffsetPx = with(LocalDensity.current) { -56.dp.toPx() }
    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != heightOffsetPx) {
            scrollBehavior.state.heightOffsetLimit = heightOffsetPx
        }
    }
    Layout(
        content = {
            if (draggable) {
                Box(Modifier.draggable(scrollBehavior)) {
                    content()
                }
            } else {
                content()
            }
        },
        modifier = modifier
    ) { measurables, constrains ->
        val placeables = ArrayList<Placeable>(measurables.size)
        var maxWidth = 0
        for(measurable in measurables) {
            val placeable = measurable.measure(constrains)
            placeables += placeable
            maxWidth = max(maxWidth, placeable.width)
        }
        layout(maxWidth, (-heightOffsetPx + scrollBehavior.state.heightOffset).toInt()) {
            placeables.forEach {
                it.place(0, scrollBehavior.state.heightOffset.toInt())
            }
        }
    }
}

@Stable
interface TopAppBarScrollBehavior {

    /**
     * A [TopAppBarState] that is attached to this behavior and is read and updated when scrolling
     * happens.
     */
    val state: TopAppBarState

    /**
     * Indicates whether the top app bar is pinned.
     *
     * A pinned app bar will stay fixed in place when content is scrolled and will not react to any
     * drag gestures.
     */
    val isPinned: Boolean

    /**
     * An optional [AnimationSpec] that defines how the top app bar snaps to either fully collapsed
     * or fully extended state when a fling or a drag scrolled it into an intermediate position.
     */
    val snapAnimationSpec: AnimationSpec<Float>?

    /**
     * An optional [DecayAnimationSpec] that defined how to fling the top app bar when the user
     * flings the app bar itself, or the content below it.
     */
    val flingAnimationSpec: DecayAnimationSpec<Float>?

    /**
     * A [NestedScrollConnection] that should be attached to a [Modifier.nestedScroll] in order to
     * keep track of the scroll events.
     */
    val nestedScrollConnection: NestedScrollConnection
}

@Composable
fun rememberTopAppBarState(
    initialHeightOffsetLimit: Float = -Float.MAX_VALUE,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f
): TopAppBarState {
    return rememberSaveable(saver = TopAppBarState.Saver) {
        TopAppBarState(
            initialHeightOffsetLimit,
            initialHeightOffset,
            initialContentOffset
        )
    }
}

/**
 * A state object that can be hoisted to control and observe the top app bar state. The state is
 * read and updated by a [TopAppBarScrollBehavior] implementation.
 *
 * In most cases, this state will be created via [rememberTopAppBarState].
 *
 * @param initialHeightOffsetLimit the initial value for [TopAppBarState.heightOffsetLimit]
 * @param initialHeightOffset the initial value for [TopAppBarState.heightOffset]
 * @param initialContentOffset the initial value for [TopAppBarState.contentOffset]
 */
@Stable
class TopAppBarState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float
) {

    /**
     * The top app bar's height offset limit in pixels, which represents the limit that a top app
     * bar is allowed to collapse to.
     *
     * Use this limit to coerce the [heightOffset] value when it's updated.
     */
    var heightOffsetLimit by mutableStateOf(initialHeightOffsetLimit)

    /**
     * The top app bar's current height offset in pixels. This height offset is applied to the fixed
     * height of the app bar to control the displayed height when content is being scrolled.
     *
     * Updates to the [heightOffset] value are coerced between zero and [heightOffsetLimit].
     */
    var heightOffset: Float
        get() = _heightOffset.value
        set(newOffset) {
            _heightOffset.value = newOffset.coerceIn(
                minimumValue = heightOffsetLimit,
                maximumValue = 0f
            )
        }

    /**
     * The total offset of the content scrolled under the top app bar.
     *
     * The content offset is used to compute the [overlappedFraction], which can later be read
     * by an implementation.
     *
     * This value is updated by a [TopAppBarScrollBehavior] whenever a nested scroll connection
     * consumes scroll events. A common implementation would update the value to be the sum of all
     * [NestedScrollConnection.onPostScroll] `consumed.y` values.
     */
    var contentOffset by mutableStateOf(initialContentOffset)

    /**
     * A value that represents the collapsed height percentage of the app bar.
     *
     * A `0.0` represents a fully expanded bar, and `1.0` represents a fully collapsed bar (computed
     * as [heightOffset] / [heightOffsetLimit]).
     */
    val collapsedFraction: Float
        get() = if (heightOffsetLimit != 0f) {
            heightOffset / heightOffsetLimit
        } else {
            0f
        }

    /**
     * A value that represents the percentage of the app bar area that is overlapping with the
     * content scrolled behind it.
     *
     * A `0.0` indicates that the app bar does not overlap any content, while `1.0` indicates that
     * the entire visible app bar area overlaps the scrolled content.
     */
    val overlappedFraction: Float
        get() = if (heightOffsetLimit != 0f) {
            1 - ((heightOffsetLimit - contentOffset).coerceIn(
                minimumValue = heightOffsetLimit,
                maximumValue = 0f
            ) / heightOffsetLimit)
        } else {
            0f
        }

    companion object {
        /**
         * The default [Saver] implementation for [TopAppBarState].
         */
        val Saver: Saver<TopAppBarState, *> = listSaver(
            save = { listOf(it.heightOffsetLimit, it.heightOffset, it.contentOffset) },
            restore = {
                TopAppBarState(
                    initialHeightOffsetLimit = it[0],
                    initialHeightOffset = it[1],
                    initialContentOffset = it[2]
                )
            }
        )
    }

    private var _heightOffset = mutableStateOf(initialHeightOffset)
}

object TopAppBarDefault {

    @Composable
    fun enterAlwaysScrollBehavior(
        state: TopAppBarState = rememberTopAppBarState(),
        canScroll: () -> Boolean = { true },
        snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
        flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay()
    ): TopAppBarScrollBehavior =
        EnterAlwaysScrollBehavior(
            state = state,
            snapAnimationSpec = snapAnimationSpec,
            flingAnimationSpec = flingAnimationSpec,
            canScroll = canScroll
        )
}

private class EnterAlwaysScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true }
) : TopAppBarScrollBehavior {
    override val isPinned: Boolean = false
    override var nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!canScroll()) return Offset.Zero
                val prevHeightOffset = state.heightOffset
                state.heightOffset = state.heightOffset + available.y
                return if (prevHeightOffset != state.heightOffset) {
                    // We're in the middle of top app bar collapse or expand.
                    // Consume only the scroll on the Y axis.
                    available.copy(x = 0f)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y
                if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
                    if (consumed.y == 0f && available.y > 0f) {
                        // Reset the total content offset to zero when scrolling all the way down.
                        // This will eliminate some float precision inaccuracies.
                        state.contentOffset = 0f
                    }
                }
                state.heightOffset = state.heightOffset + consumed.y
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val superConsumed = super.onPostFling(consumed, available)
                return superConsumed + settleAppBar(
                    state,
                    available.y,
                    flingAnimationSpec,
                    snapAnimationSpec
                )
            }
        }
}

private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    // Check if the app bar is completely collapsed/expanded. If so, no need to settle the app bar,
    // and just return Zero Velocity.
    // Note that we don't check for 0f due to float precision with the collapsedFraction
    // calculation.
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    // Snap if animation specs were provided.
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 &&
            state.heightOffset > state.heightOffsetLimit
        ) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec
            ) { state.heightOffset = value }
        }
    }

    return Velocity(0f, remainingVelocity)
}

/**
* use for TopAppBar drag
 */
private fun Modifier.draggable(scrollBehavior: TopAppBarScrollBehavior) = composed {
    draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState { delta ->
            scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset + delta
        },
        onDragStopped = { velocity ->
            settleAppBar(
                scrollBehavior.state,
                velocity,
                scrollBehavior.flingAnimationSpec,
                scrollBehavior.snapAnimationSpec
            )
        }
    )
}
