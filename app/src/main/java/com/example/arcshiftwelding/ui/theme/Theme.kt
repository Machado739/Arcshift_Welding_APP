package com.example.arcshiftwelding.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Success,
    error = Error,

    background = Background,
    surface = Surface,

    onPrimary = Surface,
    onSecondary = Surface,
    onError = Surface,

    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Success,
    error = Error,

    background = DarkBackground,
    surface = DarkSurface,

    onPrimary = Surface,
    onSecondary = Surface,
    onError = Surface,

    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

@Composable
fun ArcshiftWeldingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}