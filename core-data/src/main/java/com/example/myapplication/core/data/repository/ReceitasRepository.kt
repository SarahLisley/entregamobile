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
) {
    private val db = FirebaseDatabase.getInstance().getReference("receitas")

    suspend fun addReceita(receita: ReceitaEntity): Result<Unit> {
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

    suspend fun salvarReceita(
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
                finalImageUrl = imageStorageService.uploadImage(context, imagemUri, id)
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

    fun getReceitas(): Flow<List<ReceitaEntity>> {
        return receitaDao.getAllReceitas()
    }

    suspend fun sincronizarComFirebase(): Result<Unit> {
        if (!connectivityObserver.isConnected()) {
            return Result.failure(Exception("Sem conexão com a internet"))
        }

        return try {
            val snapshot = db.get().await()
            val receitasFirebase = mutableListOf<ReceitaEntity>()

            for (child in snapshot.children) {
                val receitaData = child.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                if (receitaData != null) {
                    val receitaEntity = receitaData.toReceitaEntity()
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

    fun escutarReceitas(onChange: (List<ReceitaEntity>) -> Unit) {
        db.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                Log.d("ReceitasRepository", "Dados recebidos do Firebase: ${snapshot.childrenCount} receitas")
                val receitasFirebase = mutableListOf<ReceitaEntity>()
                
                for (child in snapshot.children) {
                    val receitaData = child.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    if (receitaData != null) {
                        Log.d("ReceitasRepository", "Dados da receita do Firebase: $receitaData")
                        val receitaEntity = receitaData.toReceitaEntity()
                        receitasFirebase.add(receitaEntity)
                    }
                }

                Log.d("ReceitasRepository", "Total de receitas convertidas: ${receitasFirebase.size}")
                // Atualizar Room em background
                if (receitasFirebase.isNotEmpty()) {
                    // Executar em uma coroutine separada
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

    suspend fun atualizarCurtidas(id: String, curtidas: List<String>): Result<Unit> {
        return try {
            // Atualizar no Room primeiro
            receitaDao.updateCurtidas(id, curtidas)

            // Se online, atualizar no Firebase
            if (connectivityObserver.isConnected()) {
                try {
                    db.child(id).child("curtidas").setValue(curtidas).await()
                } catch (e: Exception) {
                    // Se falhar, marcar como não sincronizada
                    val receita = receitaDao.getReceitaById(id)
                    receita?.let {
                        receitaDao.updateReceita(it.copy(isSynced = false))
                    }
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }

    suspend fun atualizarFavoritos(id: String, favoritos: List<String>): Result<Unit> {
        return try {
            // Atualizar no Room primeiro
            receitaDao.updateFavoritos(id, favoritos)

            // Se online, atualizar no Firebase
            if (connectivityObserver.isConnected()) {
                try {
                    db.child(id).child("favoritos").setValue(favoritos).await()
                } catch (e: Exception) {
                    // Se falhar, marcar como não sincronizada
                    val receita = receitaDao.getReceitaById(id)
                    receita?.let {
                        receitaDao.updateReceita(it.copy(isSynced = false))
                    }
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }

    suspend fun deletarReceita(id: String, imageUrl: String?): Result<Unit> {
        return try {
            // Deletar imagem se existir
            if (!imageUrl.isNullOrBlank()) {
                imageStorageService.deleteImage(imageUrl)
            }
            
            // Deletar dados nutricionais associados
            nutritionDataDao.deleteNutritionDataByReceitaId(id)
            
            // Deletar do Room primeiro
            receitaDao.deleteReceitaById(id)

            // Se online, deletar do Firebase
            if (connectivityObserver.isConnected()) {
                try {
                    FirebaseSyncService.deleteReceita(id)
                    // Deletar dados nutricionais também
                    val nutritionData = nutritionDataDao.getNutritionDataByReceitaId(id)
                    nutritionData?.let {
                        FirebaseSyncService.deleteNutritionData(it.id)
                    }
                } catch (e: Exception) {
                    println("Erro ao deletar do Firebase: ${e.message}")
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            val userError = errorHandler.handleError(e)
            Result.failure(Exception(userError.message))
        }
    }

    suspend fun getUnsyncedReceitas(): List<ReceitaEntity> {
        return receitaDao.getUnsyncedReceitas()
    }

    suspend fun markAsSynced(receitaId: String) {
        receitaDao.markAsSynced(receitaId)
    }
    
    suspend fun getReceitaById(id: String): ReceitaEntity? {
        return receitaDao.getReceitaById(id)
    }
    
    suspend fun updateReceita(receita: ReceitaEntity): Result<Unit> {
        return try {
            receitaDao.updateReceita(receita)
            
            // Se online, sincronizar com Firebase
            if (connectivityObserver.isConnected()) {
                try {
                    FirebaseSyncService.updateReceita(receita)
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
    
    suspend fun updateImage(
        context: Context,
        newImageUri: Uri,
        oldImageUrl: String?,
        imageId: String
    ): String {
        return imageStorageService.updateImage(context, newImageUri, oldImageUrl, imageId)
    }
    
    suspend fun syncFromFirebase(): Result<Int> {
        return try {
            if (!connectivityObserver.isConnected()) {
                return Result.success(0)
            }
            
            val database = FirebaseDatabase.getInstance()
            val receitasRef = database.getReference("receitas")
            
            Log.d("ReceitasRepository", "Sincronizando receitas do Firebase...")
            val snapshot = receitasRef.get().await()
            
            if (snapshot.exists()) {
                val receitasData = snapshot.getValue(object : GenericTypeIndicator<Map<String, Map<String, Any>>>() {})
                
                if (receitasData != null) {
                    var syncCount = 0
                    
                    for ((id, receitaData) in receitasData) {
                        try {
                            val receita = createReceitaFromFirebaseData(id, receitaData)
                            
                            // Verificar se a receita já existe localmente
                            val existingReceita = receitaDao.getReceitaById(id)
                            
                            if (existingReceita == null) {
                                // Nova receita do Firebase
                                receitaDao.insertReceita(receita)
                                syncCount++
                                Log.d("ReceitasRepository", "Nova receita sincronizada do Firebase: ${receita.nome}")
                            } else if (receita.lastModified > existingReceita.lastModified) {
                                // Receita atualizada no Firebase
                                receitaDao.updateReceita(receita)
                                syncCount++
                                Log.d("ReceitasRepository", "Receita atualizada do Firebase: ${receita.nome}")
                            }
                        } catch (e: Exception) {
                            Log.e("ReceitasRepository", "Erro ao sincronizar receita $id: ${e.message}")
                        }
                    }
                    
                    Log.d("ReceitasRepository", "Total de receitas sincronizadas do Firebase: $syncCount")
                    Result.success(syncCount)
                } else {
                    Log.d("ReceitasRepository", "Nenhuma receita encontrada no Firebase")
                    Result.success(0)
                }
            } else {
                Log.d("ReceitasRepository", "Nenhuma receita encontrada no Firebase")
                Result.success(0)
            }
        } catch (e: Exception) {
            Log.e("ReceitasRepository", "Erro ao sincronizar do Firebase: ${e.message}")
            Result.failure(e)
        }
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

private fun Map<String, Any>.toReceitaEntity(): ReceitaEntity {
    val imagemUrl = this["imagemUrl"] as? String ?: ""
    val nome = this["nome"] as? String ?: ""
    
    Log.d("ReceitasRepository", "Convertendo dados do Firebase para ReceitaEntity")
    Log.d("ReceitasRepository", "Nome: $nome")
    Log.d("ReceitasRepository", "URL da imagem: '$imagemUrl'")
    Log.d("ReceitasRepository", "Dados completos: $this")
    
    return ReceitaEntity(
        id = this["id"] as? String ?: "",
        nome = nome,
        descricaoCurta = this["descricaoCurta"] as? String ?: "",
        imagemUrl = imagemUrl,
        ingredientes = (this["ingredientes"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        modoPreparo = (this["modoPreparo"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        tempoPreparo = this["tempoPreparo"] as? String ?: "",
        porcoes = (this["porcoes"] as? Number)?.toInt() ?: 1,
        userId = this["userId"] as? String ?: "",
        userEmail = this["userEmail"] as? String,
        curtidas = (this["curtidas"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        favoritos = (this["favoritos"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        isSynced = true,
        lastModified = System.currentTimeMillis()
    )
} 