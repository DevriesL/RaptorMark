package io.github.devriesl.raptormark.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.devriesl.raptormark.data.TestRepository

class BenchmarkTestViewModel(testRepo: TestRepository) : ViewModel() {

    val testName = testRepo.getTestName()
    var randLatVis = if (testRepo.isRandTest) { View.VISIBLE } else { View.GONE }
    val testResult: LiveData<String> = testRepo.testResultMutableLiveData
    val randLatResult: LiveData<String> = testRepo.randLatResultMutableLiveData
}