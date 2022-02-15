package io.github.devriesl.raptormark.ui

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.devriesl.raptormark.R

@Composable
fun AppTopBar() {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
    )
}
