package io.github.devriesl.raptormark.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    selectedSectionIndex: Int,
    sections: Array<AppSections>,
    setSelectedSection: (AppSections) -> Unit,
    closeDrawer: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sections) { section ->
            DrawerButton(
                icon = section.icon,
                title = section.title,
                isSelected = selectedSectionIndex == sections.indexOf(section),
                action = {
                    setSelectedSection(section)
                    closeDrawer()
                }
            )
        }
    }
}

@Composable
private fun DrawerButton(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    isSelected: Boolean,
    action: () -> Unit
) {
    val color = if (isSelected) {
        MaterialTheme.colors.secondary
    } else {
        LocalContentColor.current
    }

    TextButton(
        onClick = action,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color)
            )
            Spacer(Modifier.width(16.dp))
            Text(text = stringResource(title), color = color)
        }
    }
}
