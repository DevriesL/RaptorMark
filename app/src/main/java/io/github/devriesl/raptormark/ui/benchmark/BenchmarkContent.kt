package io.github.devriesl.raptormark.ui.benchmark

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.data.*
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel

@Composable
fun BenchmarkContent(
    benchmarkViewModel: BenchmarkViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val state by benchmarkViewModel.benchmarkState.collectAsState()

        LazyColumn {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            benchmarkViewModel.enableMBW(!state.enableMBWTest)
                        }
                        .heightIn(48.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = R.string.mbw_test_title))
                    Checkbox(
                        checked = state.enableMBWTest,
                        onCheckedChange = null
                    )
                }
            }
            if (state.enableMBWTest) {

                val mbwItems = benchmarkViewModel.testItems.filter { it.testCase.isMBW() }
                itemsIndexed(mbwItems) { index, testItem ->
                    val testResult by testItem.testResult.collectAsState()

                    Column {
                        MBWTestItem(
                            title = testItem.testCase.title,
                            bandwidth = (testResult as? TestResult.MBW)?.bandwidth,
                            vectorBandwidth = (testResult as? TestResult.MBW)?.vectorBandwidth,
                            isAppPerf = testItem.testCase.isMBWApp()
                        )
                        Divider(modifier = Modifier.padding(top = if (testResult == null) 0.dp else 8.dp).run {
                            if (index != mbwItems.lastIndex) {
                                padding(horizontal = 32.dp)
                            } else {
                                this
                            }
                        })
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            benchmarkViewModel.enableFIO(!state.enableFIOTest)
                        }
                        .heightIn(48.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = R.string.fio_test_title))
                    Checkbox(
                        checked = state.enableFIOTest,
                        onCheckedChange = null
                    )
                }
            }
            if (state.enableFIOTest) {
                val fioItems = benchmarkViewModel.testItems.filter { it.testCase.isFIO() }
                itemsIndexed(fioItems) { index, testItem ->
                    val testResult by testItem.testResult.collectAsState()

                    Column {
                        FIOTestItem(
                            title = testItem.testCase.title,
                            bandwidth = (testResult as? TestResult.FIO)?.bandwidth,
                            showLatency = testItem.testCase.isFIORand(),
                            latency = (testResult as? TestResult.FIO)?.latency
                        )
                        Divider(modifier = Modifier.run {
                            if (index != fioItems.lastIndex) {
                                padding(horizontal = 32.dp)
                            } else {
                                this
                            }
                        })
                    }
                }
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
                        Icon(
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
                        Icon(
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
