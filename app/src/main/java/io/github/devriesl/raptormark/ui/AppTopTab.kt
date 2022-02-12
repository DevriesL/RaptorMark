package io.github.devriesl.raptormark.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AppTopTab(
    selectedIndex: Int,
    sections: Array<AppSections>,
    setSelectedSection: (AppSections) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        elevation = AppBarDefaults.TopAppBarElevation,
        modifier = modifier
    ) {
        TabRow(
            selectedTabIndex = selectedIndex,
            backgroundColor = MaterialTheme.colors.surface,
            indicator = {
                TabRowDefaults.Indicator(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.tabIndicatorOffset(it[selectedIndex])
                )
            },
            divider = {}
        ) {
            sections.forEachIndexed { index, section ->
                Tab(
                    selected = index == selectedIndex,
                    onClick = { setSelectedSection(section) },
                    selectedContentColor = with(MaterialTheme.colors) { if (index == selectedIndex) primary else onSurface },
                    modifier = Modifier.defaultMinSize(minHeight = 56.dp),
                ) {
                    Icon(
                        painter = painterResource(id = section.icon),
                        contentDescription = stringResource(id = section.title)
                    )
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(
                        text = stringResource(id = section.title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.caption.copy(textAlign = TextAlign.Center)
                    )
                }
            }
        }
    }
}