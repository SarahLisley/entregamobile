package com.example.myapplication.core.data

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object SupabaseImageUploader {
    // Configurações do Supabase - devem ser configuradas via construtor ou método
    private var SUPABASE_URL: String = ""
    private var SUPABASE_KEY: String = ""
    private const val BUCKET = "receitas"
    
    /**
     * Configura as credenciais do Supabase
     * @param url URL do projeto Supabase
     * @param key Chave anônima do Supabase
     */
    fun configure(url: String, key: String) {
        SUPABASE_URL = url
        SUPABASE_KEY = key
    }

    suspend fun uploadBase64Image(base64Image: String, fileName: String): String? = withContext(Dispatchers.IO) {
        try {
            Log.d("SupabaseUpload", "Iniciando upload de imagem base64: $fileName")
            val cleanBase64 = if (base64Image.startsWith("data:image")) {
                base64Image.substringAfter(",")
            } else {
                base64Image
            }
            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val request = Request.Builder()
                .url("$SUPABASE_URL/storage/v1/object/$BUCKET/$fileName")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                .put(requestBody)
                .build()
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            Log.d("SupabaseUpload", "Código de resposta para base64: ${response.code}")
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                Log.e("SupabaseUpload", "Erro Supabase base64: ${response.code} - $errorBody")
                throw IOException("Erro Supabase: ${response.code} - $errorBody")
            }
            val publicUrl = "${SUPABASE_URL}/storage/v1/object/public/$BUCKET/$fileName"
            Log.d("SupabaseUpload", "Upload base64 bem-sucedido! URL pública: $publicUrl")
            return@withContext publicUrl
        } catch (e: Exception) {
            Log.e("SupabaseUpload", "Erro ao fazer upload de imagem base64: ${e.message}")
            return@withContext null
        }
    }

    suspend fun deleteImage(fileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("SupabaseUpload", "Deletando imagem do Supabase: $fileName")
            val request = Request.Builder()
                .url("$SUPABASE_URL/storage/v1/object/$BUCKET/$fileName")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                .delete()
                .build()
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            Log.d("SupabaseUpload", "Código de resposta para deletar: ${response.code}")
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                Log.e("SupabaseUpload", "Erro ao deletar imagem Supabase: ${response.code} - $errorBody")
                return@withContext false
            }
            Log.d("SupabaseUpload", "Imagem deletada com sucesso: $fileName")
            return@withContext true
        } catch (e: Exception) {
            Log.e("SupabaseUpload", "Erro ao deletar imagem do Supabase: ${e.message}")
            return@withContext false
        }
    }

    suspend fun deleteImageFromUrl(imageUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Extrair o nome do arquivo da URL
            val fileName = imageUrl.substringAfterLast("/")
            if (fileName.isBlank()) {
                Log.e("SupabaseUpload", "Não foi possível extrair o nome do arquivo da URL: $imageUrl")
                return@withContext false
            }
            return@withContext deleteImage(fileName)
        } catch (e: Exception) {
            Log.e("SupabaseUpload", "Erro ao deletar imagem da URL: ${e.message}")
            return@withContext false
        }
    }
} 