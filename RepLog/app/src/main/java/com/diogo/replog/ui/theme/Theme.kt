package com.diogo.replog.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue40,
    onPrimary = Navy99,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,

    secondary = Green40,
    onSecondary = Navy99,
    secondaryContainer = Green30,
    onSecondaryContainer = Green90,

    tertiary = Gold40,
    onTertiary = Navy5,
    tertiaryContainer = Gold30,
    onTertiaryContainer = Gold90,

    background = Navy10,
    onBackground = Navy95,

    surface = Navy15,
    onSurface = Navy95,
    surfaceVariant = Navy20,
    onSurfaceVariant = Navy80,

    error = ErrorRed,
    onError = Navy99,
    errorContainer = ErrorRedDark,
    onErrorContainer = Navy99,

    outline = Navy40,
    outlineVariant = Navy30,
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Navy99,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,

    secondary = Green40,
    onSecondary = Navy99,
    secondaryContainer = Green90,
    onSecondaryContainer = Green10,

    tertiary = Gold40,
    onTertiary = Navy5,
    tertiaryContainer = Gold90,
    onTertiaryContainer = Gold10,

    background = Navy99,
    onBackground = Navy10,

    surface = Navy99,
    onSurface = Navy10,
    surfaceVariant = Navy95,
    onSurfaceVariant = Navy30,

    error = ErrorRed,
    onError = Navy99,

    outline = Navy40,
    outlineVariant = Navy80,
)

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Suppress("DEPRECATION")
@Composable
fun RepLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RepLogTypography,
        content = content
    )
}
