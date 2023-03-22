package io.github.devriesl.raptormark.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    selectedSectionIndex: Int,
    sections: Array<AppSections>,
    setSelectedSection: (AppSections) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .windowInsetsPadding(DrawerDefaults.windowInsets)
                .height(16.dp)
        )
        sections.forEachIndexed { index, section ->
            NavigationDrawerItem(
                label = {
                    Text(text = stringResource(id = section.title))
                },
                icon = {
                    Icon(painterResource(id = section.icon), stringResource(id = section.title))
                },
                selected = selectedSectionIndex == index,
                onClick = {
                    setSelectedSection(section)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
