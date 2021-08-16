package io.github.devriesl.raptormark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import io.github.devriesl.raptormark.ui.RaptorApp

@AndroidEntryPoint
class RaptorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RaptorApp()
        }
    }
}
