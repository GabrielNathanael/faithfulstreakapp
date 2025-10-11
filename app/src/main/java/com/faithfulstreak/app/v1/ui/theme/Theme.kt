package com.faithfulstreak.app.v1.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// FaithGold Deep Blue Palette
private val FaithGold = Color(0xFFE9B84A)
private val FaithGoldDark = Color(0xFFB88B2C)
private val FaithGoldLight = Color(0xFFFFD56B)
private val FaithNavy = Color(0xFF0D1424)
private val FaithSurface = Color(0xFF141C2E)
private val FaithSurfaceVariant = Color(0xFF1E2940)
private val FaithTextLight = Color(0xFFF3EBD6)
private val FaithTextMain = Color(0xFFEAE4D0)
private val FaithSecondary = Color(0xFF3D4A63)
private val FaithError = Color(0xFFE57373)

private val FaithDarkColors: ColorScheme = darkColorScheme(
    primary = FaithGold,
    onPrimary = Color(0xFF1A1300),
    secondary = FaithSecondary,
    onSecondary = Color.White,
    tertiary = FaithGoldLight,
    background = FaithNavy,
    onBackground = FaithTextMain,
    surface = FaithSurface,
    onSurface = FaithTextLight,
    surfaceVariant = FaithSurfaceVariant,
    primaryContainer = FaithGoldDark,
    onPrimaryContainer = FaithGoldLight,
    secondaryContainer = FaithSurfaceVariant,
    onSecondaryContainer = FaithTextLight,
    error = FaithError
)

@Composable
fun FaithTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FaithDarkColors,
        typography = Typography,
        content = content
    )
}
