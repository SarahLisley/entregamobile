package com.example.myapplication.core.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

/**
 * Serviço dedicado para geração de imagens usando a API do Cloudflare Worker
 * Implementação atualizada para usar URLs públicas do Supabase em vez de Data URLs
 */
class ImageGenerationService {
    
    companion object {
        private const val WORKER_URL = "https://text-to-image-template.izaelnunesred.workers.dev"
        private const val TAG = "ImageGenerationService"
        
        // Timeouts otimizados
        private const val CONNECT_TIMEOUT = 10000L // 10s para conexão
        private const val READ_TIMEOUT = 30000L // 30s para leitura
        private const val TEST_TIMEOUT = 10000L // 10s para testes
        
        // Teste da URL do worker
        fun testWorkerUrl() {
            Log.d(TAG, "🔗 Testando URL do Worker: $WORKER_URL")
        }
    }
    
    /**
     * Gera uma imagem para uma receita usando o Cloudflare Worker
     * @param recipeName Nome da receita para gerar a imagem
     * @return URL pública da imagem gerada ou fallback
     */
    suspend fun generateRecipeImage(recipeName: String): String {
        return withContext(Dispatchers.IO) {
            generateRecipeImageWithRetry(recipeName, maxRetries = 2)
        }
    }

    private suspend fun generateRecipeImageWithRetry(recipeName: String, maxRetries: Int): String {
        var lastException: Exception? = null
        
        repeat(maxRetries) { attempt ->
            try {
                Log.d(TAG, "=== TENTATIVA ${attempt + 1}/$maxRetries ===")
                Log.d(TAG, "Receita: $recipeName")
                
                val result = withTimeout(60000L) { // 60 segundos de timeout total
                    generateRecipeImageInternal(recipeName)
                }
                Log.d(TAG, "✅ Geração bem-sucedida na tentativa ${attempt + 1}")
                return result
                
            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "❌ Tentativa ${attempt + 1} falhou: ${e.message}")
                
                when (e) {
                    is CancellationException -> {
                        Log.w(TAG, "⚠️ Geração cancelada pelo usuário")
                        throw e // Re-throw para propagar o cancelamento
                    }
                    is kotlinx.coroutines.TimeoutCancellationException -> {
                        Log.e(TAG, "⏰ Timeout na tentativa ${attempt + 1}")
                        if (attempt == maxRetries - 1) {
                            Log.e(TAG, "💥 Todas as tentativas falharam por timeout")
                            return getFallbackImageUrl(recipeName)
                        }
                    }
                    else -> {
                        if (attempt < maxRetries - 1) {
                            val delay = 1000L * (attempt + 1) // Backoff linear: 1s, 2s
                            Log.d(TAG, "⏳ Aguardando ${delay}ms antes da próxima tentativa...")
                            kotlinx.coroutines.delay(delay)
                        }
                    }
                }
            }
        }
        
        Log.e(TAG, "💥 Todas as ${maxRetries} tentativas falharam")
        Log.e(TAG, "💥 Último erro: ${lastException?.message}")
        return getFallbackImageUrl(recipeName)
    }

    private suspend fun generateRecipeImageInternal(recipeName: String): String {
        try {
            Log.d(TAG, "=== INICIANDO GERAÇÃO DE IMAGEM ===")
            Log.d(TAG, "Receita: $recipeName")
                
            // Teste de conectividade básico mais rápido
            Log.d(TAG, "🌐 Testando conectividade básica...")
            try {
                val testConnection = URL("https://www.google.com").openConnection() as HttpURLConnection
                testConnection.requestMethod = "HEAD"
                testConnection.connectTimeout = 5000
                testConnection.readTimeout = 5000
                val testResponseCode = testConnection.responseCode
                Log.d(TAG, "✅ Conectividade OK (Google response: $testResponseCode)")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Problema de conectividade: ${e.message}")
                // Continuar mesmo com problema de conectividade básica
            }
                
            // Teste específico do Worker com timeout reduzido
            Log.d(TAG, "🔗 Testando conectividade com Worker...")
            val isWorkerAccessible = testWorkerConnectivity()
            if (!isWorkerAccessible) {
                Log.e(TAG, "❌ Worker não está acessível, usando fallback")
                return getFallbackImageUrl(recipeName)
            }
            Log.d(TAG, "✅ Worker está acessível, prosseguindo...")

            val encodedPrompt = URLEncoder.encode(recipeName, "UTF-8")
            val url = "$WORKER_URL?prompt=$encodedPrompt"
            Log.d(TAG, "URL do Worker: $url")

            Log.d(TAG, "🔗 Fazendo conexão HTTP...")
            val startTime = System.currentTimeMillis()
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "NutriLivre-Android/1.0")
            connection.setRequestProperty("Cache-Control", "no-cache")
            connection.setRequestProperty("Pragma", "no-cache")
            connection.connectTimeout = CONNECT_TIMEOUT.toInt()
            connection.readTimeout = READ_TIMEOUT.toInt()
            connection.useCaches = false
            connection.defaultUseCaches = false
            connection.instanceFollowRedirects = true

            Log.d(TAG, "📡 Conectando ao Worker...")
            val responseCode = connection.responseCode
            val endTime = System.currentTimeMillis()
            val responseTime = endTime - startTime
            Log.d(TAG, "📊 Response code do Worker: $responseCode")
            Log.d(TAG, "⏱️ Tempo de resposta: ${responseTime}ms (${responseTime/1000}s)")

            if (responseCode == 200) {
                Log.d(TAG, "✅ Conexão bem-sucedida, lendo resposta...")
                val responseText = connection.inputStream.reader().readText()
                Log.d(TAG, "📄 Resposta completa do Worker: $responseText")
                    
                try {
                    val responseJson = JSONObject(responseText)
                    Log.d(TAG, "🔍 JSON parseado com sucesso")

                    // Verificar se a resposta tem success: true
                    if (responseJson.optBoolean("success", false)) {
                        // Extrair a URL da imagem da resposta
                        val imageUrl = responseJson.getString("imageUrl")
                        Log.d(TAG, "🎉 IMAGEM GERADA COM SUCESSO!")
                        Log.d(TAG, "🖼️ URL da imagem: $imageUrl")
                        Log.d(TAG, "📝 Prompt original: ${responseJson.optString("prompt", "N/A")}")
                        Log.d(TAG, "🔧 Prompt aprimorado: ${responseJson.optString("enhancedPrompt", "N/A")}")
                        Log.d(TAG, "🤖 Modelo usado: ${responseJson.optString("model", "N/A")}")
                        return imageUrl
                    } else {
                        // O Worker retornou success: false
                        val errorMessage = responseJson.optString("error", "Erro desconhecido retornado pelo Worker.")
                        Log.e(TAG, "❌ Worker retornou success: false")
                        Log.e(TAG, "❌ Mensagem de erro: $errorMessage")
                        throw Exception(errorMessage)
                    }
                } catch (jsonError: Exception) {
                    Log.e(TAG, "❌ Erro ao processar JSON da resposta: ${jsonError.message}")
                    Log.e(TAG, "❌ Resposta que causou erro: $responseText")
                    throw jsonError
                }
            } else {
                Log.e(TAG, "❌ Erro HTTP: $responseCode")
                val errorBody = connection.errorStream?.reader()?.readText() ?: "Sem corpo de erro."
                Log.e(TAG, "❌ Corpo do erro: $errorBody")
                throw Exception("Erro HTTP: $responseCode - $errorBody")
            }
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> {
                    Log.w(TAG, "⚠️ Geração de imagem foi cancelada pelo usuário")
                    Log.w(TAG, "⚠️ Motivo: ${e.message}")
                    throw e // Re-throw para propagar o cancelamento
                }
                is kotlinx.coroutines.TimeoutCancellationException -> {
                    Log.e(TAG, "⏰ Timeout na geração de imagem")
                    Log.e(TAG, "⏰ Motivo: ${e.message}")
                    throw e // Re-throw para propagar o timeout
                }
                else -> {
                    Log.e(TAG, "💥 EXCEÇÃO CAPTURADA na geração de imagem")
                    Log.e(TAG, "💥 Tipo da exceção: ${e.javaClass.simpleName}")
                    Log.e(TAG, "💥 Mensagem: ${e.message}")
                    Log.e(TAG, "💥 Stack trace:")
                    e.printStackTrace()
                    Log.e(TAG, "⚠️ Usando imagem de fallback para: $recipeName")
                    return getFallbackImageUrl(recipeName)
                }
            }
        }
    }
    
    /**
     * Testa a conectividade com o worker com timeout otimizado
     * @return true se conseguir conectar, false caso contrário
     */
    suspend fun testWorkerConnectivity(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🧪 TESTE DE CONECTIVIDADE COM WORKER")
                val testUrl = "$WORKER_URL?prompt=bolo de chocolate"
                Log.d(TAG, "🔗 URL de teste: $testUrl")
                
                val connection = URL(testUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("User-Agent", "NutriLivre-Android/1.0")
                connection.setRequestProperty("Cache-Control", "no-cache")
                connection.connectTimeout = TEST_TIMEOUT.toInt()
                connection.readTimeout = TEST_TIMEOUT.toInt()
                
                val responseCode = connection.responseCode
                Log.d(TAG, "📊 Response code do teste: $responseCode")
                
                if (responseCode == 200) {
                    val responseText = connection.inputStream.reader().readText()
                    Log.d(TAG, "✅ Worker respondeu com sucesso!")
                    Log.d(TAG, "📄 Resposta do teste: $responseText")
                    
                    // Tentar parsear o JSON para verificar se está correto
                    try {
                        val responseJson = JSONObject(responseText)
                        val hasImageUrl = responseJson.has("imageUrl")
                        val hasSuccess = responseJson.has("success")
                        Log.d(TAG, "🔍 JSON válido - imageUrl: $hasImageUrl, success: $hasSuccess")
                        
                        if (hasImageUrl && hasSuccess) {
                            val imageUrl = responseJson.getString("imageUrl")
                            Log.d(TAG, "🖼️ URL da imagem no teste: $imageUrl")
                            return@withContext true
                        } else {
                            Log.e(TAG, "❌ JSON não contém campos esperados")
                            return@withContext false
                        }
                    } catch (jsonError: Exception) {
                        Log.e(TAG, "❌ Erro ao parsear JSON do teste: ${jsonError.message}")
                        return@withContext false
                    }
                } else {
                    val errorBody = connection.errorStream?.reader()?.readText() ?: "Sem corpo de erro."
                    Log.e(TAG, "❌ Worker retornou erro: $responseCode")
                    Log.e(TAG, "❌ Corpo do erro: $errorBody")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Erro no teste de conectividade: ${e.message}")
                e.printStackTrace()
                return@withContext false
            }
        }
    }

    /**
     * Teste simples para verificar se o Worker está funcionando
     * @return String com o resultado do teste
     */
    suspend fun simpleTest(): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🧪 TESTE SIMPLES DO WORKER")
                val testUrl = "$WORKER_URL?prompt=bolo de chocolate"
                Log.d(TAG, "🔗 URL de teste: $testUrl")
                
                val startTime = System.currentTimeMillis()
                val connection = URL(testUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("User-Agent", "NutriLivre-Android/1.0")
                connection.connectTimeout = TEST_TIMEOUT.toInt()
                connection.readTimeout = TEST_TIMEOUT.toInt()
                
                val responseCode = connection.responseCode
                val responseText = connection.inputStream.reader().readText()
                val endTime = System.currentTimeMillis()
                val responseTime = endTime - startTime
                
                Log.d(TAG, "📊 Status: $responseCode")
                Log.d(TAG, "⏱️ Tempo: ${responseTime}ms")
                Log.d(TAG, "📝 Resposta: $responseText")
                
                "SUCCESS: $responseCode (${responseTime}ms) - $responseText"
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro no teste: ${e.message}")
                "ERROR: ${e.message}"
            }
        }
    }

    /**
     * Gera uma URL de fallback para imagens com múltiplas opções
     * @param recipeName Nome da receita
     * @return URL de fallback
     */
    private fun getFallbackImageUrl(recipeName: String): String {
        val sanitizedName = recipeName.replace(" ", "").replace("[^a-zA-Z0-9]".toRegex(), "")
        
        // Lista de fallbacks em ordem de preferência
        val fallbacks = listOf(
            // Unsplash - imagens de comida de alta qualidade
            "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=300&fit=crop",
            // Picsum com seed baseado no nome da receita
            "https://picsum.photos/seed/${sanitizedName}/400/300",
            // Imagem genérica de comida
            "https://images.unsplash.com/photo-1504674900242-87b0b7b3b8c8?w=400&h=300&fit=crop"
        )
        
        // Usar o primeiro fallback disponível
        return fallbacks.first()
    }
} 