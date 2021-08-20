package io.github.devriesl.raptormark.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BenchmarkViewModel @Inject constructor(
    private val settingDataSource: SettingDataSource
) : ViewModel() {
    @Volatile
    private var forceStop = false;
    private val mutableBenchmarkState = MutableStateFlow(BenchmarkState())

    val testItems: List<BenchmarkTest> =
        TestCases.values().map { BenchmarkTest(it, settingDataSource) }

    val benchmarkState: StateFlow<BenchmarkState>
        get() = mutableBenchmarkState

    fun onTestStart() {
        mutableBenchmarkState.value = mutableBenchmarkState.value.copy(running = true)
        NativeDataSource.postNativeThread {
            testItems.forEach {
                try {
                    if (forceStop) return@forEach

                    it.runTest()
                } catch (ex: Exception) {
                    Log.e(it.testCases.name, "Error running test", ex)
                    return@forEach
                }
            }
            forceStop = false
            mutableBenchmarkState.value = mutableBenchmarkState.value.copy(running = false)
        }
    }

    fun onTestStop() {
        forceStop = true
    }
}

data class BenchmarkState(
    val running: Boolean = false
)
