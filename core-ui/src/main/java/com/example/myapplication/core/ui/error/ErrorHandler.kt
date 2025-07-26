package com.example.myapplication.core.ui.error

import android.content.Context
import com.example.myapplication.core.ui.error.ImageUploadException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor() {
    
    fun handleError(throwable: Throwable): UserFriendlyError {
        return when (throwable) {
            is ImageUploadException -> UserFriendlyError(
                title = "Erro no Upload",
                message = "Não foi possível fazer o upload da imagem. Verifique sua conexão e tente novamente.",
                type = ErrorType.NETWORK
            )
            is java.net.UnknownHostException -> UserFriendlyError(
                title = "Sem Conexão",
                message = "Verifique sua conexão com a internet e tente novamente.",
                type = ErrorType.NETWORK
            )
            is java.net.SocketTimeoutException -> UserFriendlyError(
                title = "Timeout",
                message = "A operação demorou muito para responder. Tente novamente.",
                type = ErrorType.NETWORK
            )
            is retrofit2.HttpException -> {
                when (throwable.code()) {
                    401 -> UserFriendlyError(
                        title = "Não Autorizado",
                        message = "Sua sessão expirou. Faça login novamente.",
                        type = ErrorType.AUTH
                    )
                    403 -> UserFriendlyError(
                        title = "Acesso Negado",
                        message = "Você não tem permissão para realizar esta ação.",
                        type = ErrorType.AUTH
                    )
                    404 -> UserFriendlyError(
                        title = "Não Encontrado",
                        message = "O recurso solicitado não foi encontrado.",
                        type = ErrorType.NOT_FOUND
                    )
                    429 -> UserFriendlyError(
                        title = "Muitas Requisições",
                        message = "Você fez muitas requisições. Aguarde um momento e tente novamente.",
                        type = ErrorType.RATE_LIMIT
                    )
                    500, 502, 503, 504 -> UserFriendlyError(
                        title = "Erro do Servidor",
                        message = "Estamos enfrentando problemas técnicos. Tente novamente em alguns minutos.",
                        type = ErrorType.SERVER
                    )
                    else -> UserFriendlyError(
                        title = "Erro de Rede",
                        message = "Ocorreu um erro inesperado. Tente novamente.",
                        type = ErrorType.NETWORK
                    )
                }
            }
            else -> UserFriendlyError(
                title = "Erro Inesperado",
                message = "Algo deu errado. Tente novamente ou entre em contato com o suporte.",
                type = ErrorType.UNKNOWN
            )
        }
    }
    
    fun shouldRetry(throwable: Throwable): Boolean {
        return when (throwable) {
            is java.net.UnknownHostException,
            is java.net.SocketTimeoutException,
            is retrofit2.HttpException -> {
                val httpException = throwable as? retrofit2.HttpException
                httpException?.code() in listOf(408, 429, 500, 502, 503, 504)
            }
            else -> false
        }
    }
} 