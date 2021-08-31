package io.github.devriesl.raptormark.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.accompanist.insets.ProvideWindowInsets
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.ui.benchmark.BenchmarkContent
import io.github.devriesl.raptormark.ui.history.HistoryContent
import io.github.devriesl.raptormark.ui.setting.SettingContent
import io.github.devriesl.raptormark.ui.theme.RaptorMarkTheme
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel
import io.github.devriesl.raptormark.viewmodels.HistoryViewModel
import io.github.devriesl.raptormark.viewmodels.SettingViewModel
import kotlinx.coroutines.launch

@Composable
fun RaptorApp(
    benchmarkViewModel: BenchmarkViewModel,
    historyViewModel: HistoryViewModel,
    settingViewModel: SettingViewModel
) {
    ProvideWindowInsets {
        RaptorMarkTheme {
            val (selectedSection, setSelectedSection) = remember { mutableStateOf(AppSections.BENCHMARK) }
            val sections = AppSections.values()
            val selectedSectionIndex = sections.indexOfFirst { it == selectedSection }

            val coroutineScope = rememberCoroutineScope()
            val scaffoldState = rememberScaffoldState()

            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    AppTopBar(
                        openDrawer = { coroutineScope.launch { scaffoldState.drawerState.open() } }
                    )
                },
                drawerContent = {
                    AppDrawer(
                        selectedSectionIndex = selectedSectionIndex,
                        sections = sections,
                        setSelectedSection = setSelectedSection,
                        closeDrawer = { coroutineScope.launch { scaffoldState.drawerState.close() } }
                    )
                },
                content = {
                    when (selectedSection) {
                        AppSections.BENCHMARK -> BenchmarkContent(benchmarkViewModel)
                        AppSections.HISTORY -> HistoryContent(historyViewModel)
                        AppSections.SETTING -> SettingContent(settingViewModel)
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
