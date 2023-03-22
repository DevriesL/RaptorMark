package io.github.devriesl.raptormark.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DialogContent(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    buttons: (@Composable () -> Unit) = {},
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    buttonContentColor: Color = MaterialTheme.colorScheme.primary,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    content: @Composable (() -> Unit)?
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
    ) {
        val horizontalPadding = DialogContentDefaults.DIALOG_PADDING.calculateStartPadding(
            LocalLayoutDirection.current
        )
        Column(
            modifier = Modifier
                .sizeIn(minWidth = DialogContentDefaults.DIALOG_MIN_WIDTH, maxWidth = DialogContentDefaults.DIALOG_MAX_WIDTH)
                .padding(top = DialogContentDefaults.DIALOG_PADDING.calculateTopPadding())
                .heightIn(max = DialogContentDefaults.DIALOG_MAX_HEIGHT)
        ) {
            icon?.let {
                CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                    Box(
                        Modifier
                            .padding(DialogContentDefaults.ICON_PADDING)
                            .padding(horizontal = horizontalPadding)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        icon()
                    }
                }
            }
            title?.let {
                CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                    val textStyle = MaterialTheme.typography.headlineSmall
                    ProvideTextStyle(textStyle) {
                        Box(
                            // Align the title to the center when an icon is present.
                            Modifier
                                .padding(DialogContentDefaults.TITLE_PADDING)
                                .padding(horizontal = horizontalPadding)
                                .align(
                                    if (icon == null) {
                                        Alignment.Start
                                    } else {
                                        Alignment.CenterHorizontally
                                    }
                                )
                        ) {
                            title()
                        }
                    }
                }
            }
            content?.let {
                CompositionLocalProvider(LocalContentColor provides textContentColor) {
                    val textStyle =
                        MaterialTheme.typography.bodyMedium
                    ProvideTextStyle(textStyle) {
                        Box(
                            Modifier
                                .weight(weight = 1f, fill = false)
                                .align(Alignment.Start)
                        ) {
                            content()
                        }
                    }
                }
            }
            Box(modifier = Modifier.padding(horizontal = horizontalPadding).align(Alignment.End)) {
                CompositionLocalProvider(LocalContentColor provides buttonContentColor) {
                    val textStyle =
                        MaterialTheme.typography.labelLarge
                    ProvideTextStyle(value = textStyle, content = buttons)
                }
            }
        }
    }
}

object DialogContentDefaults {
    val DIALOG_MAX_HEIGHT = 480.dp
    val DIALOG_MIN_WIDTH = 280.dp
    val DIALOG_MAX_WIDTH = 560.dp
    val DIALOG_PADDING = PaddingValues(all = 24.dp)
    val ICON_PADDING = PaddingValues(bottom = 16.dp)
    val TITLE_PADDING = PaddingValues(bottom = 16.dp)
    val CONTENT_PADDING = PaddingValues(bottom = 24.dp)
}