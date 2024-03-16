package io.github.devriesl.raptormark.ui.benchmark

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
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
import kotlinx.coroutines.launch

@Composable
fun BenchmarkContent(
    benchmarkViewModel: BenchmarkViewModel,
    snackbarHostState: SnackbarHostState
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val state = benchmarkViewModel.benchmarkState

        val coroutineScope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        val hideOnScrollState = rememberHideOnScrollState()
        val nestedScrollConnection = remember(hideOnScrollState) {
            HideOnScrollNestedScrollConnection(hideOnScrollState) { true }
        }
        var floatingActionButtonHeight by remember {
            mutableStateOf(0)
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = with(LocalDensity.current) { floatingActionButtonHeight.toDp() } + 8.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .fillMaxHeight()
        ) {
            item {
                ExpandableCard(
                    expanded = state.enableMBWTest,
                    onExpandedChange = {
                        benchmarkViewModel.enableMBW(it)
                    },
                    title = R.string.mbw_test_title
                ) {
                    val mbwItems = remember(benchmarkViewModel.testItems) {
                        benchmarkViewModel.testItems.filter { it.testCase.isMBW() }
                    }
                    Column {
                        mbwItems.forEachIndexed { index, testItem ->
                            val testResult = testItem.testResult
                            Column {
                                MBWTestItem(
                                    title = testItem.testCase.title,
                                    bandwidth = remember(testResult) {
                                        (testResult as? TestResult.MBW)?.bandwidth
                                    },
                                    vectorBandwidth = remember(testResult) {
                                        (testResult as? TestResult.MBW)?.vectorBandwidth
                                    },
                                    isAppPerf = remember(testItem) { testItem.testCase.isMBWApp() }
                                )
                                if (index != mbwItems.lastIndex) {
                                    Divider(
                                        modifier = Modifier
                                            .padding(top = if (testResult == null) 0.dp else 8.dp)
                                            .padding(horizontal = 32.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            item {
                ExpandableCard(
                    expanded = state.enableFIOTest,
                    onExpandedChange = { benchmarkViewModel.enableFIO(it)},
                    title = R.string.fio_test_title
                ) {

                    val fioItems = remember(benchmarkViewModel.testItems) {
                        benchmarkViewModel.testItems.filter { it.testCase.isFIO() }
                    }
                    fioItems.forEachIndexed { index, testItem ->
                        val testResult = testItem.testResult

                        Column {
                            FIOTestItem(
                                title = testItem.testCase.title,
                                bandwidth = remember(testResult) { (testResult as? TestResult.FIO)?.bandwidth },
                                showLatency = remember(testItem) { testItem.testCase.isFIORand() },
                                latency = remember(testResult) { (testResult as? TestResult.FIO)?.latency }
                            )
                            if (index != fioItems.lastIndex) {
                                Divider(modifier = Modifier.padding(horizontal = 32.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                    .padding(top = 8.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
            ) {
                if (state.running) {
                    val tooltipString = stringResource(R.string.stop_click_tooltip)
                    ExtendedFloatingActionButton(
                        text = { Text(stringResource(R.string.stop_button_label)) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_stop_button),
                                contentDescription = stringResource(R.string.stop_button_desc)
                            )
                        },
                        onClick = {
                            benchmarkViewModel.onTestStop()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(tooltipString)
                            }
                        }
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

@Composable
fun ExpandableCard(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    expandedContent: @Composable ColumnScope.() -> Unit
) {
    val expandedState = updateTransition(targetState = expanded, "expandedCard")
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    Card(modifier = modifier) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = containerColor,
                headlineColor = contentColor,
                trailingIconColor = containerColor
            ),
            headlineContent = {
                Text(text = stringResource(id = title))
            },
            trailingContent = {
                Checkbox(checked = expandedState.targetState, onCheckedChange = null)
            },
            modifier = Modifier.clickable {
                onExpandedChange(!expanded)
            }
        )
        AnimatedVisibility(expandedState.targetState) {
            Column(
                content = expandedContent
            )
        }
    }
}
