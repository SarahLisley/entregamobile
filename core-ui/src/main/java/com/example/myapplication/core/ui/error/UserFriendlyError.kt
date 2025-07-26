package com.example.myapplication.core.ui.error

data class UserFriendlyError(
    val title: String,
    val message: String,
    val type: ErrorType
)

enum class ErrorType {
    NETWORK,
    AUTH,
    NOT_FOUND,
    RATE_LIMIT,
    SERVER,
    UNKNOWN
} 