package io.github.devriesl.raptormark.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.devriesl.raptormark.data.TestRecord
import io.github.devriesl.raptormark.data.TestRecordRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val testRecordRepo: TestRecordRepo
) : ViewModel() {
    var testRecords: List<TestRecord> by mutableStateOf(emptyList())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            testRecordRepo.testRecords.collect {
                testRecords = it
            }
        }
    }
}
