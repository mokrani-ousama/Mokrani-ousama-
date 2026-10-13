package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DiawissGold,
    onPrimary = DiawissNavyDark,
    primaryContainer = DiawissNavyLight,
    onPrimaryContainer = DiawissGoldLight,
    secondary = DiawissGoldLight,
    onSecondary = DiawissNavyDark,
    background = DiawissNavyDark,
    onBackground = DiawissWhite,
    surface = DiawissNavy,
    onSurface = DiawissWhite,
    surfaceVariant = DiawissNavyLight,
    onSurfaceVariant = DiawissGoldLight,
    outline = DiawissGoldDark
)

private val LightColorScheme = lightColorScheme(
    primary = DiawissNavy,
    onPrimary = DiawissWhite,
    primaryContainer = DiawissNavyLight,
    onPrimaryContainer = DiawissGoldLight,
    secondary = DiawissGold,
    onSecondary = DiawissNavyDark,
    secondaryContainer = DiawissGoldContainer,
    onSecondaryContainer = DiawissNavyDark,
    tertiary = DiawissGoldDark,
    onTertiary = DiawissWhite,
    background = DiawissOffWhite,
    onBackground = DiawissTextPrimary,
    surface = DiawissWhite,
    onSurface = DiawissTextPrimary,
    surfaceVariant = DiawissSurfaceVariant,
    onSurfaceVariant = DiawissTextSecondary,
    outline = DiawissBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep DIAWISS brand palette authentic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
