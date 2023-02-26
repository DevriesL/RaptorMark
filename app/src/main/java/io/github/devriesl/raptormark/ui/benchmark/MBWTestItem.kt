package io.github.devriesl.raptormark.ui.benchmark

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun MBWTestItem(
    @StringRes title: Int,
    bandwidth: List<Pair<Int, Int>>?,
    vectorBandwidth: List<Pair<Int, Int>>?,
    showChart: Boolean
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}
