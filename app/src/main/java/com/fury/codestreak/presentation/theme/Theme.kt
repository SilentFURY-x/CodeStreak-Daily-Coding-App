package com.fury.codestreak.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// We define ONLY a dark scheme because the app is "Dark Mode" by design
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextWhite,
    secondary = SurfaceHighlight,
    onSecondary = TextWhite,
    background = BackgroundDark,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    error = ErrorRed,
    onError = TextWhite
)

@Composable
fun CodeStreakTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set the Status Bar (Top) to match the background
            window.statusBarColor = BackgroundDark.toArgb()
            // Set the Navigation Bar (Bottom) to match the background
            window.navigationBarColor = BackgroundDark.toArgb()

            // Ensure icons are light (since background is dark)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}