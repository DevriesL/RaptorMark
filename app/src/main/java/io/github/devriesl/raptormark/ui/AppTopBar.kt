package io.github.devriesl.raptormark.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
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
                    painter = painterResource(id = R.drawable.ic_auto_graph_24),
                    contentDescription = stringResource(id = R.string.contributors_dialog_title)
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
