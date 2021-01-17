package io.github.devriesl.raptormark.viewmodels

import androidx.lifecycle.ViewModel
import io.github.devriesl.raptormark.data.BenchmarkTestRepository
import io.github.devriesl.raptormark.data.TestBaseJNI

class BenchmarkTestViewModel(jni: TestBaseJNI) : ViewModel() {
    private val benchmarkTestRepository: BenchmarkTestRepository = BenchmarkTestRepository(jni)

    val testName = benchmarkTestRepository.getName()
}