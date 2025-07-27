package com.example.myapplication.core.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    }
    
    /**
     * Gera uma imagem para uma receita usando o Cloudflare Worker
     * @param recipeName Nome da receita para gerar a imagem
     * @return URL pública da imagem gerada ou fallback
     */
    suspend fun generateRecipeImage(recipeName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "=== INICIANDO GERAÇÃO DE IMAGEM ===")
                Log.d(TAG, "Receita: $recipeName")
                
                // Usar o Cloudflare Worker para gerar imagem
                val encodedPrompt = URLEncoder.encode(recipeName, "UTF-8")
                val url = "$WORKER_URL?prompt=$encodedPrompt"
                
                Log.d(TAG, "URL do Worker: $url")
                
                try {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.setRequestProperty("Accept", "application/json")
                    connection.setRequestProperty("User-Agent", "NutriLivre-Android/1.0")
                    connection.connectTimeout = 30000
                    connection.readTimeout = 30000
                    
                    val responseCode = connection.responseCode
                    Log.d(TAG, "Response code do Worker: $responseCode")
                    
                    if (responseCode == 200) {
                        val responseText = connection.inputStream.reader().readText()
                        Log.d(TAG, "Resposta do Worker: $responseText")
                        
                        try {
                            val response = JSONObject(responseText)
                            
                            if (response.getBoolean("success")) {
                                val imageUrl = response.getString("imageUrl")
                                Log.d(TAG, "✅ IMAGEM GERADA COM SUCESSO!")
                                Log.d(TAG, "URL da imagem: $imageUrl")
                                Log.d(TAG, "Prompt original: ${response.optString("prompt", "N/A")}")
                                Log.d(TAG, "Modelo usado: ${response.optString("model", "N/A")}")
                                
                                return@withContext imageUrl
                            } else {
                                Log.e(TAG, "❌ Worker retornou success: false")
                            }
                        } catch (jsonError: Exception) {
                            Log.e(TAG, "❌ Erro ao processar JSON da resposta: ${jsonError.message}")
                        }
                    } else {
                        Log.e(TAG, "❌ Response code não é 200: $responseCode")
                        val errorBody = connection.errorStream?.reader()?.readText()
                        Log.e(TAG, "Erro do Worker: $errorBody")
                    }
                } catch (networkError: Exception) {
                    Log.e(TAG, "❌ Erro de rede: ${networkError.message}")
                }
                
                // Fallback para imagem placeholder
                Log.w(TAG, "⚠️ Usando imagem placeholder como fallback")
                val fallbackUrl = getFallbackImageUrl(recipeName)
                Log.d(TAG, "Fallback URL: $fallbackUrl")
                return@withContext fallbackUrl
                
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao gerar imagem: ${e.message}")
                // Fallback para imagem padrão
                return@withContext getFallbackImageUrl(recipeName)
            }
        }
    }
    
    /**
     * Gera uma URL de fallback para imagens
     * @param recipeName Nome da receita
     * @return URL de fallback
     */
    private fun getFallbackImageUrl(recipeName: String): String {
        val sanitizedName = recipeName.replace(" ", "").replace("[^a-zA-Z0-9]".toRegex(), "")
        return "https://picsum.photos/seed/${sanitizedName}/400/300"
    }
} 