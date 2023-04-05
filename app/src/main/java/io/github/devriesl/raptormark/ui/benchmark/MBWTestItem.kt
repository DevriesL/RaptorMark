package io.github.devriesl.raptormark.ui.benchmark

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R
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

    val bandwidthColor = Color(0xFF025DF4)
    val vectorBandwidthColor = Color(0xFF21A97A)

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            val expanded = (shouldShowChart && isTestCompleted.not()) || expandState == true
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(enabled = shouldShowChart && isTestCompleted, onClick = {
                        expandState = (expandState == true).not()
                    })
                    .height(48.dp)
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                if (shouldShowChart) {
                    val rotate by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        tint = LocalContentColor.current.copy(0.6f),
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = rotate
                        }
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
            ) {
                LineChart(
                    linesData = listOf(
                        bandwidthColor to bandwidth.orEmpty(),
                        vectorBandwidthColor to vectorBandwidth.orEmpty(),
                    ),
                    maxYValue = maxYValue,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        //Workaround for y label text padding
                        .padding(start = 54.dp, end = 32.dp)
                        .fillMaxWidth()
                        .height(128.dp)
                )
            }
            if (isTestCompleted) {
                val horizontalPadding = 32.dp
                Row(
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding)
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            if (isAppPerf) {
                                R.string.memset_title
                            } else {
                                R.string.non_vector_title
                            }
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${
                            (if (isAppPerf) {
                                bandwidth?.getOrNull(0)?.second
                            } else {
                                bandwidth?.takeLast(4)?.map { it.second }?.average()?.toInt()
                            } ?: 0) / 10000f
                        } GB/s",
                        color = bandwidthColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding)
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            if (isAppPerf) {
                                R.string.memcpy_title
                            } else {
                                R.string.neon_vector_title
                            }
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${
                            (if (isAppPerf) {
                                bandwidth?.getOrNull(1)?.second
                            } else {
                                vectorBandwidth?.takeLast(4)?.map { it.second }?.average()?.toInt()
                            } ?: 0) / 10000f
                        } GB/s",
                        color = vectorBandwidthColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

fun List<Pair<Int, Int>>?.maxYValue(): Int {
    val maxValue = this?.maxByOrNull { it.second }?.second ?: 0
    return (maxValue / BANDWIDTH_STEP + 1) * BANDWIDTH_STEP
}