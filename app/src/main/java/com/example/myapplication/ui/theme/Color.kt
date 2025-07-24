package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val GreenLight = Color(0xFFEAF8EE) // Fundo
val GreenMedium = Color(0xFF6FCF97) // Destaques
val GreenCard = Color(0xFFF6FBF7) // Card
val OrangeFav = Color(0xFFFF8A65) // Favoritos
val GrayText = Color(0xFF333333) // Títulos
val GrayDesc = Color(0xFF757575) // Descrições
val White = Color(0xFFFFFFFF)

val LightColors = lightColorScheme(
    primary   = GreenMedium,
    secondary = OrangeFav,
    background= GreenLight,
    surface   = GreenCard,
    onPrimary = White,
    onSecondary = White,
    onBackground = GrayText,
    onSurface = GrayText,
    tertiary = GreenMedium,
)

val DarkColors = darkColorScheme(
    primary   = GreenMedium,
    secondary = OrangeFav,
    background= Color(0xFF181A20),
    surface   = Color(0xFF23272F),
    onPrimary = White,
    onSecondary = White,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    tertiary = GreenMedium,
)
