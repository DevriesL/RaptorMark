package io.github.devriesl.raptormark.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.data.network.LoadState
import io.github.devriesl.raptormark.viewmodels.MainViewModel

@Composable
fun ContributorsDialog(
    mainViewModel: MainViewModel,
    closeDialog: () -> Unit
) {
    Dialog(onDismissRequest = { closeDialog.invoke() }) {
        DialogContent(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val contributorListState = rememberLazyListState()
            val isListOnTop by remember {
                derivedStateOf { contributorListState.firstVisibleItemIndex == 0 && contributorListState.firstVisibleItemScrollOffset == 0 }
            }
            Column(
                modifier = Modifier.height(480.dp)
            ) {
                Box {
                    DialogHeader(
                        text = stringResource(id = R.string.contributors_dialog_title),
                        modifier = Modifier.padding(horizontal = DialogHeaderDefaults.HEADER_HORIZONTAL_PADDING)
                    )
                    val targetThickness by animateDpAsState(
                        targetValue = if (isListOnTop) {
                            Dp.Unspecified
                        } else {
                            1.dp
                        }
                    )
                    Divider(
                        thickness = targetThickness,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }
                LazyColumn(
                    state = contributorListState,
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier
                        .weight(1f)
                ) {
                    if (mainViewModel.loadState != LoadState.NotLoad) {
                        item(
                            key = "load_button",
                            contentType = "load_button"
                        ) {
                            Button(
                                onClick = { mainViewModel.updateContributors() },
                                enabled = mainViewModel.loadState == LoadState.Failed,
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
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
                    }
                    items(
                        items = mainViewModel.contributorList,
                        contentType = { "contributor" },
                        key = { contributor -> contributor.id }
                    ) { contributor ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(56.dp)
                                .fillParentMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(50)),
                                model = contributor.avatarUrl,
                                contentDescription = contributor.userName
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = contributor.userName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
