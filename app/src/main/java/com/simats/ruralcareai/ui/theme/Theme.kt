package com.simats.ruralcareai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RuralPrimaryDark,
    onPrimary = RuralOnPrimaryDark,
    primaryContainer = RuralPrimaryContainerDark,
    onPrimaryContainer = RuralOnPrimaryContainerDark,
    secondary = RuralSecondaryDark,
    onSecondary = RuralOnSecondaryDark,
    secondaryContainer = RuralSecondaryContainerDark,
    onSecondaryContainer = RuralOnSecondaryContainerDark,
    tertiary = RuralTertiaryDark,
    onTertiary = RuralOnTertiaryDark,
    tertiaryContainer = RuralTertiaryContainerDark,
    onTertiaryContainer = RuralOnTertiaryContainerDark,
    background = RuralBackgroundDark,
    onBackground = RuralOnBackgroundDark,
    surface = RuralSurfaceDark,
    onSurface = RuralOnSurfaceDark,
    outline = RuralOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = RuralPrimaryLight,
    onPrimary = RuralOnPrimaryLight,
    primaryContainer = RuralPrimaryContainerLight,
    onPrimaryContainer = RuralOnPrimaryContainerLight,
    secondary = RuralSecondaryLight,
    onSecondary = RuralOnSecondaryLight,
    secondaryContainer = RuralSecondaryContainerLight,
    onSecondaryContainer = RuralOnSecondaryContainerLight,
    tertiary = RuralTertiaryLight,
    onTertiary = RuralOnTertiaryLight,
    tertiaryContainer = RuralTertiaryContainerLight,
    onTertiaryContainer = RuralOnTertiaryContainerLight,
    background = RuralBackgroundLight,
    onBackground = RuralOnBackgroundLight,
    surface = RuralSurfaceLight,
    onSurface = RuralOnSurfaceLight,
    outline = RuralOutlineLight
)

@Composable
fun RuralCareAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}