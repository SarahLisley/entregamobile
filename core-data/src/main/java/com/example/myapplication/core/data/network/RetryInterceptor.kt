package com.example.myapplication.core.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class RetryInterceptor @Inject constructor() : Interceptor {
    
    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_DELAY_MS = 1000L
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var exception: IOException? = null
        
        repeat(MAX_RETRIES) { attempt ->
            try {
                response = chain.proceed(request)
                
                // Se a resposta foi bem-sucedida, retornar imediatamente
                if (response!!.isSuccessful) {
                    return response!!
                }
                
                // Se não foi bem-sucedida mas não é um erro de rede, não tentar novamente
                if (!isRetryableError(response!!.code)) {
                    return response!!
                }
                
                // Fechar a resposta atual antes de tentar novamente
                response!!.close()
                
            } catch (e: IOException) {
                exception = e
                
                // Se não é o último tentativa, aguardar antes de tentar novamente
                if (attempt < MAX_RETRIES - 1) {
                    val delay = INITIAL_DELAY_MS * (1L shl attempt) // Backoff exponencial
                    try {
                        Thread.sleep(delay)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw IOException("Interrupted during retry", e)
                    }
                }
            }
        }
        
        // Se chegou aqui, todas as tentativas falharam
        return response ?: throw exception ?: IOException("Unknown error")
    }
    
    private fun isRetryableError(code: Int): Boolean {
        return when (code) {
            408, // Request Timeout
            429, // Too Many Requests
            500, // Internal Server Error
            502, // Bad Gateway
            503, // Service Unavailable
            504  // Gateway Timeout
            -> true
            else -> false
        }
    }
} 