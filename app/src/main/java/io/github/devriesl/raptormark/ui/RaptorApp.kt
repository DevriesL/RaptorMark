package io.github.devriesl.raptormark.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.ui.benchmark.BenchmarkContent
import io.github.devriesl.raptormark.ui.history.HistoryContent
import io.github.devriesl.raptormark.ui.setting.SettingContent
import io.github.devriesl.raptormark.ui.theme.RaptorMarkTheme
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel
import io.github.devriesl.raptormark.viewmodels.HistoryViewModel
import io.github.devriesl.raptormark.viewmodels.MainViewModel
import io.github.devriesl.raptormark.viewmodels.SettingViewModel

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun RaptorApp(
    mainViewModel: MainViewModel,
    benchmarkViewModel: BenchmarkViewModel,
    historyViewModel: HistoryViewModel,
    settingViewModel: SettingViewModel
) {
    RaptorMarkTheme {
        val (selectedSection, setSelectedSection) = rememberSaveable(stateSaver = enumSaver()) {
            mutableStateOf(
                AppSections.BENCHMARK
            )
        }
        val sections = AppSections.values()
        val selectedIndex by remember(selectedSection) {
            derivedStateOf { sections.indexOf(selectedSection) }
        }
        val saveableStateHolder = rememberSaveableStateHolder()


        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val windowSizeClass = remember(maxWidth, maxHeight) {
                WindowSizeClass.calculateFromSize(DpSize(maxWidth, maxHeight))
            }
            val windowWidth = maxWidth
            val isWidthCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
            val topAppBarScrollBehavior = if (isWidthCompact) {
                TopAppBarDefaults.enterAlwaysScrollBehavior()
            } else {
                TopAppBarDefaults.pinnedScrollBehavior()
            }

            val snackbarHostState = remember {
                SnackbarHostState()
            }
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                topBar = {
                    Column {
                        if (isWidthCompact) {
                            AppTopBar(mainViewModel = mainViewModel, topAppBarScrollBehavior)
                            AppTopTab(
                                selectedIndex = selectedIndex,
                                sections = sections,
                                setSelectedSection = setSelectedSection,
                                scrollBehavior = topAppBarScrollBehavior
                            )
                        } else {
                            AppTopBar(mainViewModel = mainViewModel, topAppBarScrollBehavior)
                        }
                    }
                },
                content = { scaffoldPadding ->
                    Row(
                        modifier = Modifier
                            .padding(scaffoldPadding)
                            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                    ) {
                        if (!isWidthCompact) {
                            // Material 3 guide: using drawer when width >= 1240.dp
                            if (windowWidth >= 1240.dp) {
                                AppDrawer(
                                    selectedSectionIndex = selectedIndex,
                                    sections = sections,
                                    setSelectedSection = setSelectedSection,
                                    modifier = Modifier
                                )
                            } else {
                                AppNavigationRail(
                                    selectedSectionIndex = selectedIndex,
                                    sections = sections,
                                    setSelectedSection = setSelectedSection,
                                )
                            }
                        }
                        Box {
                            saveableStateHolder.SaveableStateProvider(selectedSection) {
                                when (selectedSection) {
                                    AppSections.BENCHMARK -> BenchmarkContent(benchmarkViewModel, snackbarHostState)
                                    AppSections.HISTORY -> HistoryContent(
                                        historyViewModel,
                                        isWidthCompact
                                    )

                                    AppSections.SETTING -> SettingContent(
                                        settingViewModel,
                                        isWidthCompact
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

enum class AppSections(
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    BENCHMARK(R.string.benchmark_page_title, R.drawable.ic_benchmark_tab),
    HISTORY(R.string.history_page_title, R.drawable.ic_history_tab),
    SETTING(R.string.setting_page_title, R.drawable.ic_setting_tab)
}

inline fun <reified Type : Enum<Type>> enumSaver() = Saver<Type, String>(
    save = { it.name },
    restore = { enumValueOf<Type>(it) }
)
