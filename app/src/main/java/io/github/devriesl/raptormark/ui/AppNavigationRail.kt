package io.github.devriesl.raptormark.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AppNavigationRail(
    selectedSectionIndex: Int,
    sections: Array<AppSections>,
    setSelectedSection: (AppSections) -> Unit,
) {
    NavigationRail {
        sections.forEachIndexed { index, section ->
            val labelString = stringResource(id = section.title)
            NavigationRailItem(
                selected = index == selectedSectionIndex,
                icon = {
                    Icon(
                        painter = painterResource(id = section.icon),
                        contentDescription = labelString
                    )
                },
                label = {
                    Text(
                        text = labelString,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                onClick = { setSelectedSection(section) }
            )
        }
    }

}