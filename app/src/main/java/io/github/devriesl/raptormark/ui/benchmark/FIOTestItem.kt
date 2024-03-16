package io.github.devriesl.raptormark.ui.benchmark

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R

@Composable
fun FIOTestItem(
    @StringRes title: Int,
    bandwidth: Int?,
    showLatency: Boolean,
    latency: Int?
) {
    var twoLine by remember {
        mutableStateOf(false)
    }
    val lineCount = when {
        !showLatency && !twoLine -> 1
        !showLatency || !twoLine -> 2
        else -> 3
    }
    val minHeight = when (lineCount) {
        1 -> 48.dp
        2 -> 64.dp
        else -> 88.dp
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .defaultMinSize(minHeight = minHeight)
            .fillMaxWidth()
    ) {
        val bandwidthText = if (bandwidth != null) {
            stringResource(R.string.sum_of_bw_test_result_format, bandwidth)
        } else {
            String()
        }

        Row(
            modifier = Modifier.run {
                if (twoLine || showLatency) {
                    paddingFromBaseline(28.dp)
                } else {
                    align(Alignment.CenterStart)
                }
            }
        ) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleMedium,
                onTextLayout = {
                    twoLine = it.lineCount > 1
                },
                modifier = Modifier.weight(1f)
            )
            Text(
                text = bandwidthText,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(2f)
                    .alignByBaseline()
            )
        }
        if (showLatency) {
            val latencyText = if (latency != null) {
                stringResource(R.string.avg_of_4n_lat_result_format, latency)
            } else {
                String()
            }
            Row(
                modifier = Modifier.paddingFromBaseline(if (twoLine) 68.dp else 48.dp)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Text(
                        text = stringResource(id = R.string.rand_4n_lat_title),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = latencyText,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
        }
    }
}
