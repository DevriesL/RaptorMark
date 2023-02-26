package io.github.devriesl.raptormark.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BenchmarkViewModel @Inject constructor(
    private val settingSharedPrefs: SettingSharedPrefs,
    private val testRecordRepo: TestRecordRepo
) : ViewModel() {
    @Volatile
    private var forceStop = false
    private val mutableBenchmarkState = MutableStateFlow(BenchmarkState())
    var onTestStateChanged: ((Boolean) -> Unit)? = null

    var testItems: List<BenchmarkTest> = updateTestItems(false)

    val benchmarkState: StateFlow<BenchmarkState>
        get() = mutableBenchmarkState

    fun onTestStart() {
        onTestStateChanged?.invoke(true)
        mutableBenchmarkState.value = mutableBenchmarkState.value.copy(running = true)
        val testRecord = TestRecord()
        NativeHandler.postNativeThread {
            testItems.forEach {
                try {
                    if (forceStop) return@forEach

                    testRecord.setResult(it.testCase, it.runTest() ?: return@forEach)
                } catch (ex: Exception) {
                    Log.e(it.testCase.name, "Error running test", ex)
                    return@forEach
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                testRecordRepo.insertTestRecord(testRecord)
            }
            forceStop = false
            mutableBenchmarkState.value = mutableBenchmarkState.value.copy(running = false)
        }
    }

    fun onTestStop() {
        forceStop = true
        onTestStateChanged?.invoke(false)
    }

    fun enableMBW(enable: Boolean) {
        if (benchmarkState.value.running) return
        mutableBenchmarkState.value = mutableBenchmarkState.value.copy(enableMBWTest = enable)
        testItems = updateTestItems()
    }

    fun enableFIO(enable: Boolean) {
        if (benchmarkState.value.running) return
        mutableBenchmarkState.value = mutableBenchmarkState.value.copy(enableFIOTest = enable)
        testItems = updateTestItems()
    }

    private fun updateTestItems(isInitialized: Boolean = true): List<BenchmarkTest> {
        return TestCases.values().mapNotNull { testCase ->
            when {
                testCase.isMBW() && benchmarkState.value.enableMBWTest -> {
                    if (isInitialized) {
                        testItems.find { it.testCase == testCase }
                    } else {
                        null
                    } ?: MBWTest(testCase, settingSharedPrefs)
                }
                testCase.isFIO() && benchmarkState.value.enableFIOTest -> {
                    if (isInitialized) {
                        testItems.find { it.testCase == testCase }
                    } else {
                        null
                    } ?: FIOTest(testCase, settingSharedPrefs)
                }
                else -> null
            }
        }
    }
}

data class BenchmarkState(
    val running: Boolean = false,
    val enableMBWTest: Boolean = true,
    val enableFIOTest: Boolean = true
)
