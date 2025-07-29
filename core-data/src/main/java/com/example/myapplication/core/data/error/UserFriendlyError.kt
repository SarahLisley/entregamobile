package com.example.myapplication.core.data.error

/**
 * Erro amigável ao usuário com mensagem localizada
 */
data class UserFriendlyError(
    val message: String,
    val title: String = "Erro",
    val type: ErrorType = ErrorType.GENERAL
)

/**
 * Tipos de erro para categorização
 */
enum class ErrorType {
    NETWORK,
    AUTHENTICATION,
    PERMISSION,
    VALIDATION,
    SERVER,
    GENERAL
} 