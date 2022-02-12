package io.github.devriesl.raptormark.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.ui.benchmark.BenchmarkContent
import io.github.devriesl.raptormark.ui.history.HistoryContent
import io.github.devriesl.raptormark.ui.setting.SettingContent
import io.github.devriesl.raptormark.ui.theme.RaptorMarkTheme
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel
import io.github.devriesl.raptormark.viewmodels.HistoryViewModel
import io.github.devriesl.raptormark.viewmodels.SettingViewModel

@Composable
fun RaptorApp(
    benchmarkViewModel: BenchmarkViewModel,
    historyViewModel: HistoryViewModel,
    settingViewModel: SettingViewModel
) {
    RaptorMarkTheme {
        val (selectedSection, setSelectedSection) = remember { mutableStateOf(AppSections.BENCHMARK) }
        val sections = AppSections.values()
        val selectedIndex by remember(selectedSection) {
            derivedStateOf { sections.indexOf(selectedSection) }
        }
        val saveableStateHolder = rememberSaveableStateHolder()
        Scaffold(
            topBar = {
                Column {
                    AppTopBar()
                    AppTopTab(
                        selectedIndex = selectedIndex,
                        sections = sections,
                        setSelectedSection = setSelectedSection
                    )
                }
            },
            content = { scaffoldPadding ->
                Box(modifier = Modifier.padding(scaffoldPadding)) {
                    saveableStateHolder.SaveableStateProvider(selectedSection) {
                        when (selectedSection) {
                            AppSections.BENCHMARK -> BenchmarkContent(benchmarkViewModel)
                            AppSections.HISTORY -> HistoryContent(historyViewModel)
                            AppSections.SETTING -> SettingContent(settingViewModel)
                        }
                    }
                }
            }
        )
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
