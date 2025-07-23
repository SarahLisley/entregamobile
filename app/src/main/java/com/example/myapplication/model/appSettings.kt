package com.example.myapplication.model

data class AppSettings(
    val darkModeEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val primaryColor: String? = null,
    val secondaryColor: String? = null
)
