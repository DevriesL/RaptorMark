package io.github.devriesl.raptormark.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.data.TestRecord
import io.github.devriesl.raptormark.data.isMBW
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
                color = if (expandState) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            val rotate by animateFloatAsState(targetValue = if (expandState) 180f else 0f)
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                tint = LocalContentColor.current.copy(0.6f),
                contentDescription = null,
                modifier = Modifier.graphicsLayer {
                    rotationZ = rotate
                }
            )
        }

        AnimatedVisibility(expandState) {
            Column {
                testRecord.results.entries.forEachIndexed { index, (testCase, result) ->
                    TestItem(testCase, result)
                    if (testCase.isMBW()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (index < testRecord.results.size - 1) {
                        Divider(modifier = Modifier.padding(horizontal = 32.dp))
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}