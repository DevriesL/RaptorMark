package io.github.devriesl.raptormark.ui.setting

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.devriesl.raptormark.BuildConfig
import io.github.devriesl.raptormark.R

@Composable
fun AboutInfoDialog(
    @StringRes title: Int,
    openEmail: (Context, String) -> Unit,
    openWeiboUser: (Context, String) -> Unit,
    itemIndex: Int,
    closeDialog: (Int, String?) -> Unit,
) {
    val context = LocalContext.current
    val uidValue = stringResource(R.string.about_author_weibo_uid)
    val emailAddress = stringResource(R.string.about_author_email)

    Dialog(onDismissRequest = { closeDialog(itemIndex, null) }) {
        DialogContent(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column {

                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .defaultMinSize(minHeight = 40.dp)
                        .wrapContentHeight(Alignment.Bottom)
                )
                fun Modifier.normalTextModifier(): Modifier {
                    return Modifier
                        .padding(horizontal = 16.dp)
                        .then(this)
                }

                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.app_name))
                        append(" ")
                        append(stringResource(R.string.about_version_text))
                        append(" ")
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.SemiBold)
                        ) {
                            append(BuildConfig.VERSION_NAME)
                        }
                    },
                    modifier = Modifier.normalTextModifier()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.about_author_text))
                        append(" ")
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.SemiBold)
                        ) {
                            append(stringResource(R.string.about_author_name))
                        }
                    },
                    modifier = Modifier.normalTextModifier()
                )
                Spacer(modifier = Modifier.height(4.dp))

                fun Modifier.clickableModifier(
                    onClick: () -> Unit
                ): Modifier {
                    return Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .padding(
                            vertical = 4.dp,
                            horizontal = 16.dp
                        )
                        .then(this)
                }

                Text(
                    text = emailAddress,
                    modifier = Modifier
                        .clickableModifier { openEmail(context, emailAddress) }
                )
                Text(
                    text = stringResource(R.string.about_author_weibo),
                    modifier = Modifier
                        .clickableModifier { openWeiboUser(context, uidValue) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
