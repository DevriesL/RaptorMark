package io.github.devriesl.raptormark.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.data.network.LoadState
import io.github.devriesl.raptormark.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributorsDialog(
    mainViewModel: MainViewModel,
    closeDialog: () -> Unit
) {
    Dialog(onDismissRequest = closeDialog){
        DialogContent(
            title = {
                Text(text = stringResource(id = R.string.contributors_dialog_title))
            },
            modifier = Modifier
                .heightIn(max = DialogContentDefaults.DIALOG_MAX_HEIGHT)
                .padding(vertical = 32.dp)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val contributorListState = rememberLazyListState()
                if (mainViewModel.loadState != LoadState.NotLoad) {
                    Button(
                        onClick = { mainViewModel.updateContributors() },
                        enabled = mainViewModel.loadState == LoadState.Failed,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .animateContentSize()
                    ) {
                        val isLoading = mainViewModel.loadState == LoadState.Loading
                        if (isLoading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .padding(end = ButtonDefaults.IconSpacing)
                                    .size(ButtonDefaults.IconSize)
                            )
                        }
                        Text(
                            text = if (isLoading) {
                                stringResource(R.string.state_loading_description)
                            } else {
                                stringResource(R.string.state_loading_failed_click_retry_description)
                            }
                        )
                    }
                }
                LazyColumn(
                    state = contributorListState,
                    contentPadding = DialogContentDefaults.CONTENT_PADDING
                ) {
                    if (mainViewModel.loadState == LoadState.NotLoad) {
                        items(
                            items = mainViewModel.contributorList,
                            contentType = { "contributor" },
                            key = { it.id }
                        ) { contributor ->
                            ListItem(
                                headlineText = {
                                    Text(text = contributor.userName)
                                },
                                leadingContent = {
                                    AsyncImage(
                                        model = contributor.avatarUrl,
                                        contentDescription = contributor.userName,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(50))
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
                ScrollableDivider(state = contributorListState)
            }
        }
    }
}
