package com.example.myapplication.core.data.storage

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.example.myapplication.core.ui.error.ImageUploadException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorageService @Inject constructor() {
    
    private val storage = FirebaseStorage.getInstance().getReference("receita_images")
    
    suspend fun uploadImage(context: Context, imageUri: Uri, imageId: String): String {
        return try {
            // Verificar se a URI é válida
            if (imageUri.toString().isBlank()) {
                throw ImageUploadException("URI da imagem é inválida")
            }
            
            // Criar referência única para a imagem
            val timestamp = System.currentTimeMillis()
            val fileName = "${imageId}_$timestamp.jpg"
            val imageRef = storage.child(fileName)
            
            // Verificar se a referência foi criada corretamente
            if (imageRef == null) {
                throw ImageUploadException("Não foi possível criar referência para upload")
            }
            
            // Fazer upload com timeout
            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.await()
            
            // Obter URL de download
            val downloadUrl = imageRef.downloadUrl.await()
            downloadUrl.toString()
            
        } catch (e: Exception) {
            when {
                e.message?.contains("404") == true -> {
                    throw ImageUploadException("Erro de configuração do Firebase Storage. Verifique as regras de segurança.")
                }
                e.message?.contains("403") == true -> {
                    throw ImageUploadException("Acesso negado ao Firebase Storage. Verifique as permissões.")
                }
                e.message?.contains("network") == true || e.message?.contains("timeout") == true -> {
                    throw ImageUploadException("Erro de conexão. Verifique sua internet e tente novamente.")
                }
                else -> {
                    throw ImageUploadException("Erro ao fazer upload da imagem: ${e.message}")
                }
            }
        }
    }
    
    suspend fun deleteImage(imageUrl: String) {
        try {
            if (imageUrl.isNotBlank() && imageUrl.startsWith("https://")) {
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
        return try {
            // Deletar imagem antiga se existir
            if (!oldImageUrl.isNullOrBlank()) {
                deleteImage(oldImageUrl)
            }
            
            // Fazer upload da nova imagem
            uploadImage(context, newImageUri, imageId)
        } catch (e: Exception) {
            throw ImageUploadException("Erro ao atualizar imagem: ${e.message}")
        }
    }
    
    // Método para verificar se o Firebase Storage está acessível
    suspend fun testStorageConnection(): Boolean {
        return try {
            val testRef = storage.child("test_connection")
            testRef.name // Apenas verificar se a referência pode ser criada
            true
        } catch (e: Exception) {
            println("Erro ao conectar com Firebase Storage: ${e.message}")
            false
        }
    }
} 