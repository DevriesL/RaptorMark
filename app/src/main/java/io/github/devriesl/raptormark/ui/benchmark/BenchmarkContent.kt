package io.github.devriesl.raptormark.ui.benchmark

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel

@Composable
fun BenchmarkContent(
    benchmarkViewModel: BenchmarkViewModel
) {
    LazyColumn {
        items(benchmarkViewModel.testItems) { testItem ->
            val testResult by testItem.testResult.collectAsState()

            TestItem(
                title = testItem.testCases.title,
                bandwidth = testResult.bandwidth,
                showLatency = testItem.testCases.isRand,
                latency = testResult.latency
            )
        }
    }
}
