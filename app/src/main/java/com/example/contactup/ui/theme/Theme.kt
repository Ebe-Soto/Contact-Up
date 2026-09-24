package com.example.contactup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = BackgroundLight,
    secondaryContainer = SurfaceVariantLight,
    background = BackgroundLight,
    onBackground = TextPLight,
    surface = BackgroundLight,
    onSurface = TextPLight,
    surfaceVariant = BotBackgroundLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = TextPDark,
    secondaryContainer = SurfaceVariantDark,
    background = BackgroundDark,
    onBackground = TextPDark,
    surface = BackgroundDark,
    onSurface = TextPDark,
    surfaceVariant = BotBackgroundDark
)

@Composable
fun ContactUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}