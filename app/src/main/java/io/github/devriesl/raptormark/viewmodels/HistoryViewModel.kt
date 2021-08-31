package io.github.devriesl.raptormark.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.TestRecord
import io.github.devriesl.raptormark.data.TestRecordRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val testRecordRepo: TestRecordRepo
) : ViewModel() {
    private val mutableTestRecords = MutableStateFlow<List<TestRecord>>(emptyList())
    val testRecords: StateFlow<List<TestRecord>> = mutableTestRecords

    init {
        viewModelScope.launch(Dispatchers.IO) {
            testRecordRepo.testRecords.collect {
                mutableTestRecords.value = it
            }
        }
    }
}
