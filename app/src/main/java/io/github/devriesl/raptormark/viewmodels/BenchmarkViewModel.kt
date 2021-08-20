package io.github.devriesl.raptormark.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.BenchmarkTest
import io.github.devriesl.raptormark.data.SettingDataSource
import io.github.devriesl.raptormark.data.TestCases
import javax.inject.Inject

@HiltViewModel
class BenchmarkViewModel @Inject constructor(
    private val settingDataSource: SettingDataSource
) : ViewModel() {
    val testItems: List<BenchmarkTest> = TestCases.values().map { BenchmarkTest(it, settingDataSource) }
}
