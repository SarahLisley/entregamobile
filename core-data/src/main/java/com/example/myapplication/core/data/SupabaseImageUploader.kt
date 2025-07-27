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
    private const val SUPABASE_URL = "https://your-project.supabase.co"
    private const val SUPABASE_KEY = "your-anon-key"
    private const val BUCKET = "receitas"

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
} 