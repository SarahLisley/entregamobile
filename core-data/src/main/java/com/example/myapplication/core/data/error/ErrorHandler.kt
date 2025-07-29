package com.example.myapplication.core.data.error

import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manipulador de erros centralizado para converter exceções em erros amigáveis
 */
@Singleton
class ErrorHandler @Inject constructor() {
    
    fun handleError(throwable: Throwable): UserFriendlyError {
        return when (throwable) {
            is ImageUploadException -> {
                UserFriendlyError(
                    message = throwable.message ?: "Erro ao fazer upload da imagem",
                    title = "Erro de Upload",
                    type = ErrorType.VALIDATION
                )
            }
            is HttpException -> {
                when (throwable.code()) {
                    401 -> UserFriendlyError(
                        message = "Sessão expirada. Faça login novamente.",
                        title = "Erro de Autenticação",
                        type = ErrorType.AUTHENTICATION
                    )
                    403 -> UserFriendlyError(
                        message = "Você não tem permissão para realizar esta ação.",
                        title = "Acesso Negado",
                        type = ErrorType.PERMISSION
                    )
                    404 -> UserFriendlyError(
                        message = "Recurso não encontrado.",
                        title = "Não Encontrado",
                        type = ErrorType.VALIDATION
                    )
                    500 -> UserFriendlyError(
                        message = "Erro interno do servidor. Tente novamente mais tarde.",
                        title = "Erro do Servidor",
                        type = ErrorType.SERVER
                    )
                    else -> UserFriendlyError(
                        message = "Erro de comunicação com o servidor.",
                        title = "Erro de Rede",
                        type = ErrorType.NETWORK
                    )
                }
            }
            is SocketTimeoutException -> {
                UserFriendlyError(
                    message = "Tempo limite de conexão excedido. Verifique sua internet.",
                    title = "Timeout",
                    type = ErrorType.NETWORK
                )
            }
            is UnknownHostException -> {
                UserFriendlyError(
                    message = "Sem conexão com a internet. Verifique sua rede.",
                    title = "Sem Conexão",
                    type = ErrorType.NETWORK
                )
            }
            else -> {
                UserFriendlyError(
                    message = throwable.message ?: "Ocorreu um erro inesperado.",
                    title = "Erro",
                    type = ErrorType.GENERAL
                )
            }
        }
    }
} 