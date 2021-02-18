package io.github.devriesl.raptormark.viewmodels

import androidx.lifecycle.ViewModel
import io.github.devriesl.raptormark.data.TestRepository

class BenchmarkTestViewModel(testRepo: TestRepository) : ViewModel() {

    val testName = testRepo.getTestName()
}