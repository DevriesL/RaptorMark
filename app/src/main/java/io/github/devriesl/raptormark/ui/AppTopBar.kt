package io.github.devriesl.raptormark.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.ui.widget.ContributorsDialog
import io.github.devriesl.raptormark.viewmodels.MainViewModel

@Composable
fun AppTopBar(
    mainViewModel: MainViewModel
) {
    var openDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = {
                mainViewModel.updateContributors()
                openDialog = true
            }) {
                Icon(
                    imageVector = Icons.Filled.AutoGraph,
                    contentDescription = Icons.Filled.AutoGraph.name
                )
            }
        }
    )

    if (openDialog) {
        ContributorsDialog(mainViewModel = mainViewModel) {
            openDialog = false
        }
    }
}
