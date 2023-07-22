package io.github.devriesl.raptormark

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.devriesl.raptormark.ui.RaptorApp
import io.github.devriesl.raptormark.viewmodels.BenchmarkViewModel
import io.github.devriesl.raptormark.viewmodels.HistoryViewModel
import io.github.devriesl.raptormark.viewmodels.MainViewModel
import io.github.devriesl.raptormark.viewmodels.SettingViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RaptorActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val benchmarkViewModel: BenchmarkViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            val view = LocalView.current
            LaunchedEffect(key1 = isDarkTheme) {
                window.statusBarColor = 0x00000000
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !isDarkTheme
                    isAppearanceLightNavigationBars = !isDarkTheme
                }
            }
            RaptorApp(
                mainViewModel = mainViewModel,
                benchmarkViewModel = benchmarkViewModel,
                historyViewModel = historyViewModel,
                settingViewModel = settingViewModel
            )
        }
        lifecycleScope.launch {
            snapshotFlow {
                benchmarkViewModel.benchmarkState.running
            }.flowWithLifecycle(lifecycle)
                .collectLatest { running ->
                    if (running) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }
        }
    }
}
