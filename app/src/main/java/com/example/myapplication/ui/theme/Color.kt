package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


val LightColors = lightColorScheme(
    primary   = Color(0xFF6750A4),
    secondary = Color(0xFF625B71),
    background= Color(0xFFFFFBFE),
    surface   = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    /* adicione mais cores se desejar */
)

val DarkColors = darkColorScheme(
    primary   = Color(0xFFD0BCFF),
    secondary = Color(0xFFCCC2DC),
    background= Color(0xFF1C1B1F),
    surface   = Color(0xFF1C1B1F),
    onPrimary = Color(0xFF381E72),
    onBackground = Color(0xFFE6E1E5),
    /* ... */
)
