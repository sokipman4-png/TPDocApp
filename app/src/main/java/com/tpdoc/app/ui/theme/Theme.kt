package com.tpdoc.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HijauTp = Color(0xFF0B6E4F)
private val HijauTpTua = Color(0xFF085A40)
private val Emas = Color(0xFFC9A227)
private val AbuRingan = Color(0xFFF3F5F4)

private val LightColors = lightColorScheme(
    primary = HijauTp,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDEBDE),
    onPrimaryContainer = HijauTpTua,
    secondary = Emas,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF6E8C3),
    onSecondaryContainer = Color(0xFF5C470B),
    background = AbuRingan,
    onBackground = Color(0xFF1B1F1D),
    surface = Color.White,
    onSurface = Color(0xFF1B1F1D),
    surfaceVariant = Color(0xFFE7ECE9),
    onSurfaceVariant = Color(0xFF434846),
    error = Color(0xFFBA1A1A),
)

@Composable
fun TPDocTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}