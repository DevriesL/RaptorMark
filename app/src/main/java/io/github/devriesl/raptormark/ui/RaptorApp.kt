package io.github.devriesl.raptormark.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.ui.benchmark.BenchmarkContent
import io.github.devriesl.raptormark.ui.history.HistoryContent
import io.github.devriesl.raptormark.ui.setting.SettingContent
import io.github.devriesl.raptormark.ui.theme.RaptorMarkTheme
import io.github.devriesl.raptormark.ui.widget.*
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel
import io.github.devriesl.raptormark.viewmodels.HistoryViewModel
import io.github.devriesl.raptormark.viewmodels.MainViewModel
import io.github.devriesl.raptormark.viewmodels.SettingViewModel

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
        val topAppBarScrollBehavior = TopAppBarDefault.enterAlwaysScrollBehavior()

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val widthClass = rememberSizeClass(size = maxWidth)
            Scaffold(
                topBar = {
                    Column {
                        if (widthClass == SizeClass.Compact) {
                            ScrollableTopAppBar(scrollBehavior = topAppBarScrollBehavior) {
                                AppTopBar(mainViewModel = mainViewModel)
                            }
                            AppTopTab(
                                selectedIndex = selectedIndex,
                                sections = sections,
                                setSelectedSection = setSelectedSection
                            )
                        } else {
                            AppTopBar(mainViewModel = mainViewModel)
                        }
                    }
                },
                content = { scaffoldPadding ->
                    val modifier = if (widthClass == SizeClass.Compact) {
                        Modifier
                            .padding(scaffoldPadding)
                            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                    } else {
                        Modifier
                            .padding(scaffoldPadding)
                    }
                    Row(modifier = modifier) {
                        if (widthClass != SizeClass.Compact) {
                            NavigationRail(
                                backgroundColor = MaterialTheme.colors.surface
                            ) {
                                if (widthClass == SizeClass.Expanded) {
                                    AppDrawer(
                                        selectedSectionIndex = selectedIndex,
                                        sections = sections,
                                        setSelectedSection = setSelectedSection,
                                        modifier = Modifier
                                            .width(280.dp)
                                    )
                                } else {
                                    AppNavigationRail(
                                        selectedSectionIndex = selectedIndex,
                                        sections = sections,
                                        setSelectedSection = setSelectedSection,
                                    )
                                }
                            }
                        }
                        Box {
                            saveableStateHolder.SaveableStateProvider(selectedSection) {
                                when (selectedSection) {
                                    AppSections.BENCHMARK -> BenchmarkContent(benchmarkViewModel)
                                    AppSections.HISTORY -> HistoryContent(historyViewModel)
                                    AppSections.SETTING -> SettingContent(settingViewModel)
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
