// C:/Users/Sarah Lisley/AndroidStudioProjects/MyApplication3/app/src/main/java/com/example/myapplication/ui/theme/Theme.kt
package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    // ... suas cores escuras ...
)

private val LightColorScheme = lightColorScheme(
    // ... suas cores claras ...
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
