package io.github.devriesl.raptormark.ui.benchmark

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel

@Composable
fun BenchmarkContent(
    benchmarkViewModel: BenchmarkViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val state by benchmarkViewModel.benchmarkState.collectAsState()

        LazyColumn {
            items(benchmarkViewModel.testItems) { testItem ->
                val testResult by testItem.testResult.collectAsState()

                TestItem(
                    title = testItem.testCase.title,
                    bandwidth = testResult.bandwidth,
                    showLatency = testItem.testCase.isRand,
                    latency = testResult.latency
                )
                Divider()
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
        ) {
            if (state.running) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.stop_button_label)) },
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.ic_stop_button),
                            contentDescription = stringResource(R.string.stop_button_desc)
                        )
                    },
                    onClick = { benchmarkViewModel.onTestStop() }
                )
            } else {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.start_button_label)) },
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.ic_start_button),
                            contentDescription = stringResource(R.string.start_button_desc)
                        )
                    },
                    onClick = { benchmarkViewModel.onTestStart() }
                )
            }
        }
    }
}
