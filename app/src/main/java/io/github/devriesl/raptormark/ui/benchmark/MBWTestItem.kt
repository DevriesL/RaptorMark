package io.github.devriesl.raptormark.ui.benchmark

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.ui.widget.LineChart

const val BANDWIDTH_STEP = 80000 // 8GB

@Composable
fun MBWTestItem(
    @StringRes title: Int,
    bandwidth: List<Pair<Int, Int>>?,
    vectorBandwidth: List<Pair<Int, Int>>?,
    isAppPerf: Boolean
) {
    val isTestNotStarted = bandwidth.isNullOrEmpty() && vectorBandwidth.isNullOrEmpty()
    val isTestCompleted =
        (bandwidth?.isNotEmpty() == true && bandwidth.size == vectorBandwidth?.size) || (isAppPerf && bandwidth?.size == 2)
    val shouldShowChart = isAppPerf.not() && isTestNotStarted.not()
    var expandState by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val maxYValue = maxOf(bandwidth.maxYValue(), vectorBandwidth.maxYValue())
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(enabled = shouldShowChart && isTestCompleted, onClick = {
                        expandState = (expandState == true).not()
                    })
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.weight(1f)
                )
                if (shouldShowChart) {
                    val rotate by animateFloatAsState(
                        targetValue = if ((shouldShowChart && isTestCompleted.not()) || expandState == true) 180f else 0f
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        tint = LocalContentColor.current.copy(ContentAlpha.medium),
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = rotate
                        }
                    )
                }
            }
            if ((shouldShowChart && isTestCompleted.not()) || expandState == true) {
                LineChart(
                    linesData = listOf(
                        Color(0xFF025DF4) to bandwidth.orEmpty(),
                        Color(0xFF21A97A) to vectorBandwidth.orEmpty(),
                    ),
                    maxYValue = maxYValue,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(128.dp)
                )
            }
        }
    }
}

fun List<Pair<Int, Int>>?.maxYValue(): Int {
    val maxValue = this?.maxByOrNull { it.second }?.second ?: 0
    return (maxValue / BANDWIDTH_STEP + 1) * BANDWIDTH_STEP
}