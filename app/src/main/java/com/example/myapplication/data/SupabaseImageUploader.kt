package com.example.myapplication.data

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

object SupabaseImageUploader {
    private const val SUPABASE_URL = "https://zfbkkrtpnoteapbxfuos.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpmYmtrcnRwbm90ZWFwYnhmdW9zIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTMzNzgxMzIsImV4cCI6MjA2ODk1NDEzMn0.-hvEHVZY08vBKkFlK3fqIBhOs1_8HzIzGCop2OurB_U"
    private const val BUCKET = "receitas"

    suspend fun uploadImage(context: Context, imageUri: Uri): String? = withContext(Dispatchers.IO) {
        Log.d("SupabaseUpload", "Iniciando upload da imagem: $imageUri")
        val bytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() } ?: run {
            Log.e("SupabaseUpload", "Falha ao abrir InputStream para $imageUri")
            return@withContext null
        }
        val fileName = "receita_${System.currentTimeMillis()}.jpg"
        Log.d("SupabaseUpload", "Nome do arquivo gerado: $fileName")

        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), bytes)
        val request = Request.Builder()
            .url("$SUPABASE_URL/storage/v1/object/$BUCKET/$fileName")
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .put(requestBody)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()
        Log.d("SupabaseUpload", "Código de resposta: ${response.code}")
        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            Log.e("SupabaseUpload", "Erro Supabase: ${response.code} - $errorBody")
            throw IOException("Erro Supabase: ${response.code} - $errorBody")
        }
        val publicUrl = "${SUPABASE_URL}/storage/v1/object/public/$BUCKET/$fileName"
        Log.d("SupabaseUpload", "Upload bem-sucedido! URL pública: $publicUrl")
        return@withContext publicUrl
    }

    suspend fun deleteImageByUrl(imageUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val fileName = imageUrl.substringAfterLast("/")
            val request = Request.Builder()
                .url("${SUPABASE_URL}/storage/v1/object/$BUCKET/$fileName")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                .delete()
                .build()
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SupabaseDelete", "Erro ao deletar imagem: ${e.message}")
            false
        }
    }
} 