package io.github.devriesl.raptormark.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BenchmarkViewModel @Inject constructor(
    private val settingSharedPrefs: SettingSharedPrefs,
    private val testRecordRepo: TestRecordRepo
) : ViewModel() {
    @Volatile
    private var forceStop = false

    var benchmarkState by mutableStateOf(BenchmarkState())
        private set

    var testItems: List<BenchmarkTest> = updateTestItems(false)

    fun onTestStart() {
        benchmarkState = benchmarkState.copy(running = true)
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
            benchmarkState = benchmarkState.copy(running = false)
        }
    }

    fun onTestStop() {
        forceStop = true
    }

    fun enableMBW(enable: Boolean) {
        if (benchmarkState.running) return
        benchmarkState = benchmarkState.copy(enableMBWTest = enable)
        testItems = updateTestItems()
    }

    fun enableFIO(enable: Boolean) {
        if (benchmarkState.running) return
        benchmarkState = benchmarkState.copy(enableFIOTest = enable)
        testItems = updateTestItems()
    }

    private fun updateTestItems(isInitialized: Boolean = true): List<BenchmarkTest> {
        return TestCases.values().mapNotNull { testCase ->
            when {
                testCase.isMBW() && benchmarkState.enableMBWTest -> {
                    if (isInitialized) {
                        testItems.find { it.testCase == testCase }
                    } else {
                        null
                    } ?: MBWTest(testCase, settingSharedPrefs)
                }
                testCase.isFIO() && benchmarkState.enableFIOTest -> {
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
