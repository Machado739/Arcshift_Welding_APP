package com.example.arcshiftwelding.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ArcshiftExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color
)

internal val LightArcshiftExtendedColors = ArcshiftExtendedColors(
    success = Color(0xFF15803D),
    onSuccess = Color.White,
    successContainer = Color(0xFFDCFCE7),
    onSuccessContainer = Color(0xFF14532D),
    warning = Color(0xFFD97706),
    onWarning = Color.White,
    warningContainer = Color(0xFFFEF3C7),
    onWarningContainer = Color(0xFF78350F),
    info = Color(0xFF0891B2),
    onInfo = Color.White,
    infoContainer = Color(0xFFCFFAFE),
    onInfoContainer = Color(0xFF164E63)
)

internal val DarkArcshiftExtendedColors = ArcshiftExtendedColors(
    success = Color(0xFF4ADE80),
    onSuccess = Color(0xFF052E16),
    successContainer = Color(0xFF14532D),
    onSuccessContainer = Color(0xFFDCFCE7),
    warning = Color(0xFFFBBF24),
    onWarning = Color(0xFF422006),
    warningContainer = Color(0xFF713F12),
    onWarningContainer = Color(0xFFFEF3C7),
    info = Color(0xFF67E8F9),
    onInfo = Color(0xFF083344),
    infoContainer = Color(0xFF164E63),
    onInfoContainer = Color(0xFFCFFAFE)
)

internal val LocalArcshiftExtendedColors = staticCompositionLocalOf {
    LightArcshiftExtendedColors
}

val MaterialTheme.arcshiftColors: ArcshiftExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalArcshiftExtendedColors.current
