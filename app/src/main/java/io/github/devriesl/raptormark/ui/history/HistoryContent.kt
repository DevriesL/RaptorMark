package io.github.devriesl.raptormark.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.devriesl.raptormark.viewmodels.HistoryViewModel

@Composable
fun HistoryContent(
    historyViewModel: HistoryViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn {
            items(historyViewModel.testRecords.value) { testRecord ->
                RecordItem(testRecord)
                Divider()
            }
        }
    }
}
