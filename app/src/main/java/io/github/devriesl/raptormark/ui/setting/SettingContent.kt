package io.github.devriesl.raptormark.ui.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.devriesl.raptormark.viewmodels.SettingViewModel

@Composable
fun SettingContent(
    settingViewModel: SettingViewModel,
    isWidthCompact: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val dialogContent by settingViewModel.dialogContent.collectAsState()

        LazyColumn(
            contentPadding = PaddingValues(
                vertical = 8.dp,
                horizontal = if (isWidthCompact) {
                    0.dp
                } else {
                    16.dp
                }
            ),
            modifier = Modifier.fillMaxHeight()
        ) {
            itemsIndexed(settingViewModel.settingItems) { index, settingItem ->
                val data by settingItem.data.collectAsState()

                SettingItem(
                    title = settingItem.option.title,
                    desc = settingItem.option.desc,
                    data = data,
                    openDialog = { settingViewModel.openDialog(settingItem) },
                    isCard = !isWidthCompact,
                    isFirst = index == 0,
                    isLast = index == settingViewModel.settingItems.lastIndex
                )
            }
        }

        dialogContent?.invoke()
    }
}
