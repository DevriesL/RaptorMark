package io.github.devriesl.raptormark.ui.history

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.data.TestRecord
import io.github.devriesl.raptormark.data.parseTestResult
import io.github.devriesl.raptormark.ui.benchmark.TestItem
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RecordItem(
    testRecord: TestRecord
) {
    var expandState by rememberSaveable { mutableStateOf(false) }
    val date = SimpleDateFormat(
        stringResource(R.string.test_record_date_format),
        Locale.getDefault()
    ).format(Date(Timestamp(testRecord.timestamp).time))

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { expandState = !expandState }
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = date,
                color = if (expandState) MaterialTheme.colors.primary else LocalContentColor.current,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.weight(1f)
            )
            val rotate by animateFloatAsState(targetValue = if (expandState) 180f else 0f)
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                tint = LocalContentColor.current.copy(ContentAlpha.medium),
                contentDescription = null,
                modifier = Modifier.graphicsLayer {
                    rotationZ = rotate
                }
            )
        }

        if (expandState) {
            Column {
                testRecord.getResults().toList().forEach {
                    val testCase = it.first
                    val result = it.second
                    if (result != null) {
                        val testResult = parseTestResult(result)
                        TestItem(
                            title = testCase.title,
                            bandwidth = testResult.bandwidth,
                            showLatency = testCase.isRand,
                            latency = testResult.latency
                        )
                    }
                }
            }

        }
    }
}