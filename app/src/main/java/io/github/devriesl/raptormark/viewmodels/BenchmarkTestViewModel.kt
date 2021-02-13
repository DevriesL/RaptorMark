package io.github.devriesl.raptormark.viewmodels

import androidx.lifecycle.ViewModel
import io.github.devriesl.raptormark.data.BenchmarkTestRepository
import io.github.devriesl.raptormark.data.FIONativeTest

class BenchmarkTestViewModel(nativeTest: FIONativeTest) : ViewModel() {
    private val benchmarkTestRepository: BenchmarkTestRepository = BenchmarkTestRepository(nativeTest)

    val testName = benchmarkTestRepository.getName()
}