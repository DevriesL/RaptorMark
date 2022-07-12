package io.github.devriesl.raptormark.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import io.github.devriesl.raptormark.R
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
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(480.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.contributors_dialog_title),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 40.dp)
                            .wrapContentHeight(Alignment.Top)
                    )
                }
                items(mainViewModel.contributorList) { contributor ->
                    Row(Modifier.height(64.dp)) {
                        AsyncImage(
                            modifier = Modifier.padding(4.dp),
                            model = contributor.avatarUrl,
                            contentDescription = contributor.userName
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = contributor.userName)
                    }
                }
            }
        }
    }
}
