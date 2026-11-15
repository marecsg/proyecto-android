package com.pe.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val ModernBlueColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextLight,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryDark,

    secondary = PrimaryDark,
    onSecondary = TextLight,
    secondaryContainer = PrimarySoft,
    onSecondaryContainer = PrimaryDark,

    background = BackgroundClean,
    onBackground = TextDark,

    surface = SurfaceWhite,
    onSurface = TextDark,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextMuted,

    outline = BorderStrong,
    outlineVariant = Border,

    error = ErrorText,
    onError = TextLight,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorText
)


@Immutable
data class ExtendedColors(
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val warningBorder: Color,
    val errorBorder: Color,
    val textBody: Color,
    val textHint: Color
)

private val LabExtendedColors = ExtendedColors(
    success = SuccessText,
    successContainer = SuccessContainer,
    warning = WarningText,
    warningContainer = WarningContainer,
    warningBorder = WarningBorder,
    errorBorder = ErrorBorder,
    textBody = TextBody,
    textHint = TextHint
)

private val LocalExtendedColors = staticCompositionLocalOf { LabExtendedColors }

object AppTheme {
    val extended: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalExtendedColors provides LabExtendedColors) {
        MaterialTheme(
            colorScheme = ModernBlueColorScheme,
            typography = TipografiaApp,
            content = content
        )
    }
}