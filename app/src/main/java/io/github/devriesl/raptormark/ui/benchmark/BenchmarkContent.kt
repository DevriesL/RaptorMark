package io.github.devriesl.raptormark.ui.benchmark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.data.*
import io.github.devriesl.raptormark.ui.widget.HideOnScrollNestedScrollConnection
import io.github.devriesl.raptormark.ui.widget.rememberHideOnScrollState
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel

@Composable
fun BenchmarkContent(
    benchmarkViewModel: BenchmarkViewModel
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val state by benchmarkViewModel.benchmarkState.collectAsState()

        val listState = rememberLazyListState()
        val hideOnScrollState = rememberHideOnScrollState()
        val canHide by remember(constraints.maxHeight) {
            derivedStateOf { listState.layoutInfo.viewportSize.height >= constraints.maxHeight }
        }
        val nestedScrollConnection = remember(hideOnScrollState) {
            HideOnScrollNestedScrollConnection(hideOnScrollState) { canHide }
        }
        var floatingActionButtonHeight by remember {
            mutableStateOf(0)
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = with(LocalDensity.current) { floatingActionButtonHeight.toDp() }),
            modifier = Modifier.nestedScroll(nestedScrollConnection)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = R.string.mbw_test_title))
                    Checkbox(
                        checked = state.enableMBWTest,
                        onCheckedChange = { benchmarkViewModel.enableMBW(it) })
                }
            }
            if (state.enableMBWTest) {
                items(benchmarkViewModel.testItems.filter { it.testCase.isMBW() }) { testItem ->
                    val testResult by testItem.testResult.collectAsState()

                    MBWTestItem(
                        title = testItem.testCase.title,
                        bandwidth = (testResult as? TestResult.MBW)?.bandwidth,
                        vectorBandwidth = (testResult as? TestResult.MBW)?.vectorBandwidth,
                        isAppPerf = testItem.testCase.isMBWApp()
                    )
                    Divider()
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = R.string.fio_test_title))
                    Checkbox(
                        checked = state.enableFIOTest,
                        onCheckedChange = { benchmarkViewModel.enableFIO(it) })
                }
            }
            if (state.enableFIOTest) {
                items(benchmarkViewModel.testItems.filter { it.testCase.isFIO() }) { testItem ->
                    val testResult by testItem.testResult.collectAsState()

                    FIOTestItem(
                        title = testItem.testCase.title,
                        bandwidth = (testResult as? TestResult.FIO)?.bandwidth,
                        showLatency = testItem.testCase.isFIORand(),
                        latency = (testResult as? TestResult.FIO)?.latency
                    )
                    Divider()
                }
            }
        }

        AnimatedVisibility(
            visibleState = hideOnScrollState.isShow,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .onSizeChanged {
                    floatingActionButtonHeight = it.height
                }
        ) {
            Box(
                modifier = Modifier
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
}
