package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ImmersiveDarkColorScheme = darkColorScheme(
    primary = LavenderAccent,
    onPrimary = PurpleContainer,
    primaryContainer = PurplePrimary,
    onPrimaryContainer = LightPurpleText,
    secondary = LavenderAccent,
    onSecondary = PurpleContainer,
    secondaryContainer = ImmersiveSurfaceVariant,
    onSecondaryContainer = LightPurpleText,
    tertiary = MintAccent,
    background = ImmersiveBackground,
    onBackground = TextPrimary,
    surface = ImmersiveSurface,
    onSurface = TextPrimary,
    surfaceVariant = ImmersiveSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ImmersiveBorder,
    outlineVariant = ImmersiveInputBg
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ImmersiveDarkColorScheme,
        typography = Typography,
        content = content
    )
}
