package io.github.devriesl.raptormark.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopTab(
    selectedIndex: Int,
    sections: Array<AppSections>,
    setSelectedSection: (AppSections) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    Surface(
        modifier = modifier
    ) {
        val contentColor = LocalContentColor.current
        val tabTextWidthList = remember(sections) {
            mutableStateListOf(*Array(sections.size) { 0.dp })
        }
        val density = LocalDensity.current
        val colorTransitionFraction = scrollBehavior?.state?.overlappedFraction ?: 0f
        val fraction = if (colorTransitionFraction > 0.01f) 1f else 0f
        val appBarContainerColor by animateColorAsState(
            targetValue = lerp(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceColorAtElevation(3.0.dp),
                FastOutLinearInEasing.transform(fraction)
            ),
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = appBarContainerColor,
            divider = {

            },
            indicator = {
                val currentTabPosition = it[selectedIndex]
                val tabTextWidth = tabTextWidthList[selectedIndex]
                TabRowDefaults.Indicator(
                    modifier = Modifier.composed(
                        inspectorInfo = debugInspectorInfo {
                            name = "tabIndicatorOffset"
                            value = it[selectedIndex]
                        }
                    ) {
                        val widthPadding = (currentTabPosition.width - tabTextWidth) / 2
                        val currentTabWidth by animateDpAsState(
                            targetValue = tabTextWidth,
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = FastOutSlowInEasing
                            )
                        )
                        val indicatorOffset by animateDpAsState(
                            targetValue = currentTabPosition.left + widthPadding,
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = FastOutSlowInEasing
                            )
                        )
                        fillMaxWidth()
                            .wrapContentSize(Alignment.BottomStart)
                            .offset(x = indicatorOffset)
                            .width(currentTabWidth)
                            .clip(RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
                    }
                )
            }
        ) {
            sections.forEachIndexed { index, section ->
                Tab(
                    selected = index == selectedIndex,
                    onClick = { setSelectedSection(section) },
                    selectedContentColor = if (index == selectedIndex) {
                        LocalContentColor.current
                    } else {
                        contentColor
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = section.icon),
                            contentDescription = stringResource(id = section.title)
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = section.title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.onSizeChanged {
                                tabTextWidthList[index] = with(density) { it.width.toDp() }
                            }
                        )
                    }
                )
            }
        }
    }
}