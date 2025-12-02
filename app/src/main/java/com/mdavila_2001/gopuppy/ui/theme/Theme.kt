package com.mdavila_2001.gopuppy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val OwnerLightScheme = lightColorScheme(
    primary = OwnerPrimary,
    onPrimary = OwnerOnPrimary,
    primaryContainer = OwnerContainer,
    onPrimaryContainer = OwnerOnContainer,
    secondary = OwnerPrimary,
    onSecondary = OwnerOnPrimary,
    secondaryContainer = OwnerContainer,
    onSecondaryContainer = OwnerOnContainer,
    background = NeutralBgLight,
    surface = White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    error = ErrorLight,
    errorContainer = StatusRejectedBg,
    onErrorContainer = StatusRejectedText
)

private val OwnerDarkScheme = darkColorScheme(
    primary = OwnerPrimary,
    onPrimary = OwnerOnPrimary,
    primaryContainer = OwnerOnContainer,
    onPrimaryContainer = OwnerContainer,
    secondary = OwnerPrimary,
    onSecondary = OwnerOnPrimary,
    secondaryContainer = OwnerOnContainer,
    onSecondaryContainer = OwnerContainer,
    background = NeutralBgDark,
    surface = Black,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    error = ErrorDark,
    errorContainer = StatusRejectedBg,
    onErrorContainer = StatusRejectedText
)

private val WalkerLightScheme = lightColorScheme(
    primary = WalkerPrimary,
    onPrimary = WalkerOnPrimary,
    primaryContainer = WalkerContainer,
    onPrimaryContainer = WalkerOnContainer,
    secondary = WalkerPrimary,
    onSecondary = WalkerOnPrimary,
    secondaryContainer = WalkerContainer,
    onSecondaryContainer = WalkerOnContainer,
    background = NeutralBgLight,
    surface = White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    error = ErrorLight,
    errorContainer = StatusRejectedBg,
    onErrorContainer = StatusRejectedText
)

private val WalkerDarkScheme = darkColorScheme(
    primary = WalkerPrimary,
    onPrimary = WalkerOnPrimary,
    primaryContainer = WalkerOnContainer,
    onPrimaryContainer = WalkerContainer,
    secondary = WalkerPrimary,
    onSecondary = WalkerOnPrimary,
    secondaryContainer = WalkerOnContainer,
    onSecondaryContainer = WalkerContainer,
    background = NeutralBgDark,
    surface = Black,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    error = ErrorDark,
    errorContainer = StatusRejectedBg,
    onErrorContainer = StatusRejectedText
)

@Composable
fun GoPuppyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    role: String = "owner",
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> if (role.lowercase() == "walker") WalkerDarkScheme else OwnerDarkScheme
        else -> if (role.lowercase() == "walker") WalkerLightScheme else OwnerLightScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}