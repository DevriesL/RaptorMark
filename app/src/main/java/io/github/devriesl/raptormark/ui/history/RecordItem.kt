package io.github.devriesl.raptormark.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
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
    val expandState = remember { mutableStateOf(false) }
    val date = SimpleDateFormat(
        stringResource(R.string.test_record_date_format),
        Locale.getDefault()
    ).format(Date(Timestamp(testRecord.timestamp).time))

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clickable { expandState.value = true }
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = date,
            fontSize = 20.sp
        )
        IconButton(modifier = Modifier.align(Alignment.CenterEnd),
            onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null
            )
        }
        if (expandState.value) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { expandState.value = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = 4.dp
                ) {
                    LazyColumn {
                        items(testRecord.getResults().toList()) {
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
    }
}