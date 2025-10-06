package com.faithfulstreak.app.v1.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Orange = Color(0xFFFF8A00)
private val SurfaceDark = Color(0xFF0B0B0B)
private val OnSurfaceDark = Color(0xFFFFFFFF)
private val GrayDark = Color(0xFF2C2C2C)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    secondary = Orange,
    onSecondary = Color.White,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    primaryContainer = GrayDark,
    onPrimaryContainer = OnSurfaceDark
)

@Composable
fun FaithTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}
