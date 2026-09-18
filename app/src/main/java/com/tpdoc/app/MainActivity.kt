package com.tpdoc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tpdoc.app.navigation.TPDocApp
import com.tpdoc.app.ui.theme.TPDocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPDocTheme {
                TPDocApp()
            }
        }
    }
}