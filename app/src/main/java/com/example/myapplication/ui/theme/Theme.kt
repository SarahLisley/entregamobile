// C:/Users/Sarah Lisley/AndroidStudioProjects/MyApplication3/app/src/main/java/com/example/myapplication/ui/theme/Theme.kt
package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = OrangeSecondary,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = White,
    onSecondary = White,
    onBackground = TextOnDark,
    onSurface = TextOnDark,
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = OrangeSecondary,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = White,
    onSecondary = White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun Theme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
