package io.github.devriesl.raptormark.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.viewmodels.HistoryViewModel

@Composable
fun HistoryContent(
    historyViewModel: HistoryViewModel,
    isWidthCompact: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(
                vertical = 8.dp,
                horizontal = if (isWidthCompact) {
                    0.dp
                } else {
                    16.dp
                }
            ),
            verticalArrangement = if (isWidthCompact) {
                Arrangement.Top
            } else {
                Arrangement.spacedBy(8.dp)
            },
            modifier = Modifier.fillMaxHeight()
        ) {
            items(historyViewModel.testRecords) { testRecord ->
                if (isWidthCompact) {
                    RecordItem(testRecord)
                    Divider()
                } else {
                    Card {
                        RecordItem(testRecord)
                    }
                }
            }
        }
    }
}
