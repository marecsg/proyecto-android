package com.pe.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ModernBlueColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = TextLight,
    primaryContainer = IceBlueContainer,
    onPrimaryContainer = DeepBlue,

    secondary = SoftPurple, // Usamos el morado sutil como color de acento/secundario
    onSecondary = TextLight,
    secondaryContainer = PurpleContainer,
    onSecondaryContainer = DeepBlue,

    background = BackgroundClean,
    onBackground = TextDark,

    surface = SurfaceWhite,
    onSurface = TextDark,
    surfaceVariant = SurfaceWhite,
    onSurfaceVariant = TextMuted
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ModernBlueColorScheme,
        content = content
    )
}