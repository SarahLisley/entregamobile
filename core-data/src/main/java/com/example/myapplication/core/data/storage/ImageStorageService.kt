package com.example.myapplication.core.data.storage

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.example.myapplication.core.ui.error.ImageUploadException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorageService @Inject constructor() {
    
    private val storage = FirebaseStorage.getInstance().getReference("receita_images")
    
    suspend fun uploadImage(context: Context, imageUri: Uri, imageId: String): String {
        return try {
            val imageRef = storage.child("${imageId}_${System.currentTimeMillis()}")
            val uploadTask = imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw ImageUploadException("Erro ao fazer upload da imagem: ${e.message}")
        }
    }
    
    suspend fun deleteImage(imageUrl: String) {
        try {
            if (imageUrl.isNotBlank()) {
                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                imageRef.delete().await()
            }
        } catch (e: Exception) {
            // Log do erro mas não falhar a operação principal
            println("Erro ao deletar imagem: ${e.message}")
        }
    }
    
    suspend fun updateImage(
        context: Context, 
        newImageUri: Uri, 
        oldImageUrl: String?, 
        imageId: String
    ): String {
        // Deletar imagem antiga se existir
        if (!oldImageUrl.isNullOrBlank()) {
            deleteImage(oldImageUrl)
        }
        
        // Fazer upload da nova imagem
        return uploadImage(context, newImageUri, imageId)
    }
} 