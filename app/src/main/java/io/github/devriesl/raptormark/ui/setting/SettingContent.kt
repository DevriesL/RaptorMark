package io.github.devriesl.raptormark.ui.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.github.devriesl.raptormark.viewmodels.SettingViewModel

@Composable
fun SettingContent(
    settingViewModel: SettingViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val dialogContent by settingViewModel.dialogContent.collectAsState()

        LazyColumn {
            items(settingViewModel.settingItems) { settingItem ->
                val data by settingItem.data.collectAsState()

                SettingItem(
                    title = settingItem.settingOptions.title,
                    desc = settingItem.settingOptions.desc,
                    data = data,
                    openDialog = { settingViewModel.openDialog(settingItem) }
                )
                Divider()
            }
        }

        dialogContent?.invoke()
    }
}
