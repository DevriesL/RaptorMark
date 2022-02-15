package io.github.devriesl.raptormark.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
        MaterialTheme.colors.primary
    } else {
        LocalContentColor.current
    }
    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }
    val shape = MaterialTheme.shapes.small
    Surface(
        color = Color.Transparent,
        contentColor = color,
        modifier = Modifier.fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = mutableInteractionSource,
                onClick = action
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
                .defaultMinSize(minHeight = 40.dp)
                .background(
                    color = if (isSelected) color.copy(0.12f) else Color.Transparent,
                    shape = shape
                )
                .clip(shape)
                .indication(mutableInteractionSource, LocalIndication.current)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(text = stringResource(title), style = MaterialTheme.typography.body2)
        }
    }
}
