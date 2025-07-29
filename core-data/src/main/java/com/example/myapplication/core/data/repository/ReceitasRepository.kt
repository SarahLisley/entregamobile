package com.example.myapplication.core.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.myapplication.core.data.storage.ImageStorageService
import com.example.myapplication.core.ui.error.ErrorHandler
import com.example.myapplication.core.ui.error.UserFriendlyError
import com.example.myapplication.core.data.network.ConnectivityObserver
import com.example.myapplication.core.data.database.dao.ReceitaDao
import com.example.myapplication.core.data.database.dao.NutritionDataDao
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.database.entity.NutritionDataEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.FirebaseSyncService
import com.example.myapplication.core.data.SupabaseImageUploader
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceitasRepository @Inject constructor(
    private val receitaDao: ReceitaDao,
    private val nutritionDataDao: NutritionDataDao,
    private val connectivityObserver: ConnectivityObserver,
    private val imageStorageService: ImageStorageService,
    private val errorHandler: ErrorHandler
) : IReceitasRepository {
    private val db = FirebaseDatabase.getInstance().getReference("receitas")

    override suspend fun addReceita(receita: ReceitaEntity): Result<Unit> {
        return try {
            // Salvar no Room primeiro
            receitaDao.insertReceita(receita)

            // Se online, sincronizar com Firebase
            if (connectivityObserver.isConnected()) {
                try {
                    FirebaseSyncService.syncReceita(receita)
                    receitaDao.markAsSynced(receita.id)
                } catch (e: Exception) {
                    // Se falhar, marcar como não sincronizada
                    receitaDao.updateReceita(receita.copy(isSynced = false))
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }

    override suspend fun salvarReceita(
        context: Context,
        id: String,
        nome: String,
        descricaoCurta: String,
        imagemUri: Uri?,
        ingredientes: List<String>,
        modoPreparo: List<String>,
        tempoPreparo: String,
        porcoes: Int,
        userId: String,
        userEmail: String?,
        imagemUrl: String?
    ): Result<String> {
        return try {
            var finalImageUrl = ""
            
            // Upload da imagem se fornecida
            if (imagemUri != null) {
                // Converter URI para base64 e fazer upload para Supabase
                val base64Image = context.contentResolver.openInputStream(imagemUri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                } ?: ""
                
                val fileName = "${id}_${System.currentTimeMillis()}.jpg"
                finalImageUrl = uploadImagemParaSupabase(base64Image, fileName) ?: ""
            } else if (imagemUrl != null) {
                finalImageUrl = imagemUrl
            }

            // Criar entidade para o Room
            val receitaEntity = ReceitaEntity(
                id = id,
                nome = nome,
                descricaoCurta = descricaoCurta,
                imagemUrl = finalImageUrl,
                ingredientes = ingredientes,
                modoPreparo = modoPreparo,
                tempoPreparo = tempoPreparo,
                porcoes = porcoes,
                userId = userId,
                userEmail = userEmail,
                curtidas = emptyList(),
                favoritos = emptyList(),
                isSynced = connectivityObserver.isConnected(),
                lastModified = System.currentTimeMillis()
            )

            // Salvar no Room primeiro (sempre)
            receitaDao.insertReceita(receitaEntity)

            // Se online, salvar no Firebase também
            if (connectivityObserver.isConnected()) {
                try {
                    salvarReceitaNoFirebase(receitaEntity.toMap())
                    receitaDao.markAsSynced(id)
                } catch (e: Exception) {
                    // Se falhar, marcar como não sincronizada
                    receitaDao.updateReceita(receitaEntity.copy(isSynced = false))
                }
            }

            Result.success(finalImageUrl)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }

    suspend fun salvarDadosNutricionais(
        receitaId: String,
        nutritionData: RecipeNutrition
    ): Result<Unit> {
        return try {
            val nutritionEntity = NutritionDataEntity(
                id = "${receitaId}_nutrition_${System.currentTimeMillis()}",
                receitaId = receitaId,
                calories = nutritionData.calories,
                protein = nutritionData.protein,
                fat = nutritionData.fat,
                carbohydrates = nutritionData.carbohydrates,
                fiber = nutritionData.fiber,
                sugar = nutritionData.sugar,
                isSynced = connectivityObserver.isConnected()
            )

            // Salvar no Room primeiro
            nutritionDataDao.insertNutritionData(nutritionEntity)

            // Se online, sincronizar com Firebase
            if (connectivityObserver.isConnected()) {
                try {
                    FirebaseSyncService.syncNutritionData(nutritionEntity)
                    nutritionDataDao.markAsSynced(nutritionEntity.id)
                } catch (e: Exception) {
                    // Se falhar, marcar como não sincronizada
                    nutritionDataDao.updateNutritionData(nutritionEntity.copy(isSynced = false))
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }

    suspend fun getDadosNutricionais(receitaId: String): NutritionDataEntity? {
        return nutritionDataDao.getNutritionDataByReceitaId(receitaId)
    }

    suspend fun uploadImagemParaSupabase(base64Image: String, fileName: String): String? {
        return try {
            SupabaseImageUploader.uploadBase64Image(base64Image, fileName)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun salvarReceitaNoFirebase(receitaMap: Map<String, Any?>): Result<Unit> {
        return try {
            val id = receitaMap["id"] as String
            db.child(id).setValue(receitaMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }

    override fun getReceitas(): Flow<List<ReceitaEntity>> {
        return receitaDao.getAllReceitas()
    }

    override suspend fun sincronizarComFirebase(): Result<Unit> {
        if (!connectivityObserver.isConnected()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }

        return try {
            val snapshot = db.get().await()
            val receitasFirebase = mutableListOf<ReceitaEntity>()

            for (child in snapshot.children) {
                val receitaData = child.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                if (receitaData != null) {
                    val receitaEntity = createReceitaFromFirebaseData(child.key ?: "", receitaData)
                    receitasFirebase.add(receitaEntity)
                }
            }

            // Salvar todas as receitas do Firebase no Room
            if (receitasFirebase.isNotEmpty()) {
                receitaDao.insertReceitas(receitasFirebase)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }

    override suspend fun escutarReceitas(callback: (List<ReceitaEntity>) -> Unit) {
        db.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                Log.d("ReceitasRepository", "Dados recebidos do Firebase: ${snapshot.childrenCount} receitas")
                val receitasFirebase = mutableListOf<ReceitaEntity>()
                
                for (child in snapshot.children) {
                    val receitaData = child.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    if (receitaData != null) {
                        Log.d("ReceitasRepository", "Chave do Firebase: ${child.key}")
                        Log.d("ReceitasRepository", "Dados da receita do Firebase: $receitaData")
                        val receitaEntity = createReceitaFromFirebaseData(child.key ?: "", receitaData)
                        receitasFirebase.add(receitaEntity)
                    }
                }

                Log.d("ReceitasRepository", "Total de receitas convertidas: ${receitasFirebase.size}")
                
                // Chamar o callback com os dados atualizados
                callback(receitasFirebase)
                
                // Atualizar Room em background usando o escopo fornecido pelo ViewModel
                if (receitasFirebase.isNotEmpty()) {
                    // Executar em uma coroutine separada para operações de banco de dados
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        try {
                            receitaDao.insertReceitas(receitasFirebase)
                            Log.d("ReceitasRepository", "Receitas inseridas no Room com sucesso")
                        } catch (e: Exception) {
                            // Log do erro mas não falhar a operação
                            Log.e("ReceitasRepository", "Erro ao inserir receitas no Room: ${e.message}")
                        }
                    }
                }
            }
            
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Log.e("ReceitasRepository", "Erro ao escutar Firebase: ${error.message}")
            }
        })
    }

    private fun createReceitaFromFirebaseData(id: String, data: Map<String, Any>): ReceitaEntity {
        return ReceitaEntity(
            id = id,
            nome = data["nome"] as? String ?: "",
            descricaoCurta = data["descricaoCurta"] as? String ?: "",
            imagemUrl = data["imagemUrl"] as? String ?: "",
            ingredientes = (data["ingredientes"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            modoPreparo = (data["modoPreparo"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            tempoPreparo = data["tempoPreparo"] as? String ?: "",
            porcoes = (data["porcoes"] as? Long)?.toInt() ?: 1,
            userId = data["userId"] as? String ?: "",
            userEmail = data["userEmail"] as? String,
            curtidas = (data["curtidas"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            favoritos = (data["favoritos"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            tags = (data["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            isSynced = true,
            lastModified = (data["lastModified"] as? Long) ?: System.currentTimeMillis()
        )
    }

    override suspend fun deletarReceita(id: String, imageUrl: String?): Result<Unit> {
        return try {
            // 1. Deletar do Firebase
            val firebaseDeleted = FirebaseSyncService.deleteReceita(id)
            if (!firebaseDeleted) {
                Log.w("ReceitasRepository", "Falha ao deletar do Firebase: $id")
            }

            // 2. Deletar imagem do Supabase se existir
            if (!imageUrl.isNullOrBlank()) {
                val supabaseDeleted = SupabaseImageUploader.deleteImageFromUrl(imageUrl)
                if (!supabaseDeleted) {
                    Log.w("ReceitasRepository", "Falha ao deletar imagem do Supabase: $imageUrl")
                }
            }

            // 3. Deletar do banco local
            receitaDao.deleteReceitaById(id)
            
            Log.d("ReceitasRepository", "Receita deletada com sucesso: $id")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ReceitasRepository", "Erro ao deletar receita: ${e.message}")
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }

    override suspend fun atualizarCurtidas(id: String, curtidas: List<String>): Result<Unit> {
        return try {
            val receita = receitaDao.getReceitaById(id)
            if (receita != null) {
                receitaDao.updateReceita(receita.copy(curtidas = curtidas))
                
                // Se online, sincronizar com Firebase
                if (connectivityObserver.isConnected()) {
                    try {
                        val receitaAtualizada = receita.copy(curtidas = curtidas)
                        FirebaseSyncService.syncReceita(receitaAtualizada)
                    } catch (e: Exception) {
                        Log.w("ReceitasRepository", "Erro ao sincronizar curtidas: ${e.message}")
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }
    
    override suspend fun atualizarFavoritos(id: String, favoritos: List<String>): Result<Unit> {
        return try {
            val receita = receitaDao.getReceitaById(id)
            if (receita != null) {
                receitaDao.updateReceita(receita.copy(favoritos = favoritos))
                
                // Se online, sincronizar com Firebase
                if (connectivityObserver.isConnected()) {
                    try {
                        val receitaAtualizada = receita.copy(favoritos = favoritos)
                        FirebaseSyncService.syncReceita(receitaAtualizada)
                    } catch (e: Exception) {
                        Log.w("ReceitasRepository", "Erro ao sincronizar favoritos: ${e.message}")
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }
    
    // Implementação dos métodos de verificação de permissões
    override fun canEditReceita(receita: ReceitaEntity, currentUserId: String?): Boolean {
        return currentUserId != null && receita.userId == currentUserId
    }
    
    override fun canDeleteReceita(receita: ReceitaEntity, currentUserId: String?): Boolean {
        return currentUserId != null && receita.userId == currentUserId
    }
    
    // Implementações das funções que estavam faltando
    override suspend fun getReceitaById(id: String): ReceitaEntity? {
        return receitaDao.getReceitaById(id)
    }
    
    override suspend fun updateReceita(receita: ReceitaEntity): Result<Unit> {
        return try {
            receitaDao.updateReceita(receita)
            
            // Se online, sincronizar com Firebase
            if (connectivityObserver.isConnected()) {
                try {
                    FirebaseSyncService.syncReceita(receita)
                } catch (e: Exception) {
                    Log.w("ReceitasRepository", "Erro ao sincronizar receita: ${e.message}")
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }
    
    override suspend fun deleteReceita(id: String): Result<Unit> {
        return try {
            receitaDao.deleteReceitaById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }
    
    override suspend fun clearAllLocalData(): Result<Unit> {
        return try {
            receitaDao.deleteAllReceitas()
            Log.d("ReceitasRepository", "Todos os dados locais foram limpos")
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }
    
    override suspend fun updateImage(context: Context, novaImagemUri: Uri, imagemUrlAntiga: String?, id: String): String? {
        return try {
            // Converter URI para base64 e fazer upload para Supabase
            val base64Image = context.contentResolver.openInputStream(novaImagemUri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
            } ?: ""
            
            val fileName = "${id}_${System.currentTimeMillis()}.jpg"
            val novaImagemUrl = uploadImagemParaSupabase(base64Image, fileName)
            
            novaImagemUrl ?: imagemUrlAntiga
        } catch (e: Exception) {
            imagemUrlAntiga
        }
    }
    
    override suspend fun curtirReceita(id: String, userId: String, curtidas: List<String>): Result<Unit> {
        return try {
            val receita = receitaDao.getReceitaById(id)
            if (receita != null) {
                val novasCurtidas = if (curtidas.contains(userId)) {
                    curtidas - userId
                } else {
                    curtidas + userId
                }
                receitaDao.updateReceita(receita.copy(curtidas = novasCurtidas))
                
                // Se online, sincronizar com Firebase
                if (connectivityObserver.isConnected()) {
                    try {
                        val receitaAtualizada = receita.copy(curtidas = novasCurtidas)
                        FirebaseSyncService.syncReceita(receitaAtualizada)
                    } catch (e: Exception) {
                        Log.w("ReceitasRepository", "Erro ao sincronizar curtidas: ${e.message}")
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun favoritarReceita(id: String, userId: String, favoritos: List<String>): Result<Unit> {
        return try {
            val receita = receitaDao.getReceitaById(id)
            if (receita != null) {
                val novosFavoritos = if (favoritos.contains(userId)) {
                    favoritos - userId
                } else {
                    favoritos + userId
                }
                receitaDao.updateReceita(receita.copy(favoritos = novosFavoritos))
                
                // Se online, sincronizar com Firebase
                if (connectivityObserver.isConnected()) {
                    try {
                        val receitaAtualizada = receita.copy(favoritos = novosFavoritos)
                        FirebaseSyncService.syncReceita(receitaAtualizada)
                    } catch (e: Exception) {
                        Log.w("ReceitasRepository", "Erro ao sincronizar favoritos: ${e.message}")
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun syncFromFirebase(): Result<Int> {
        return try {
            val snapshot = db.get().await()
            val receitasFirebase = snapshot.children.mapNotNull { child ->
                child.getValue(ReceitaEntity::class.java)
            }
            
            var syncCount = 0
            for (receita in receitasFirebase) {
                try {
                    receitaDao.insertReceita(receita)
                    syncCount++
                } catch (e: Exception) {
                    // Ignorar erros individuais
                }
            }
            
            Result.success(syncCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extensões para conversão de dados
private fun ReceitaEntity.toMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "nome" to nome,
        "descricaoCurta" to descricaoCurta,
        "imagemUrl" to imagemUrl,
        "ingredientes" to ingredientes,
        "modoPreparo" to modoPreparo,
        "tempoPreparo" to tempoPreparo,
        "porcoes" to porcoes,
        "userId" to userId,
        "userEmail" to userEmail,
        "curtidas" to curtidas,
        "favoritos" to favoritos
    )
} 