package com.example.myapplication.core.data.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.core.data.storage.ImageStorageService
import com.example.myapplication.core.ui.error.ErrorHandler
import com.example.myapplication.core.ui.error.UserFriendlyError
import com.example.myapplication.core.data.network.ConnectivityObserver
import com.example.myapplication.core.data.database.dao.ReceitaDao
import com.example.myapplication.core.data.database.entity.ReceitaEntity
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
    private val connectivityObserver: ConnectivityObserver,
    private val imageStorageService: ImageStorageService,
    private val errorHandler: ErrorHandler
) {
    private val db = FirebaseDatabase.getInstance().getReference("receitas")

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
                val receitasFirebase = mutableListOf<ReceitaEntity>()
                
                for (child in snapshot.children) {
                    val receitaData = child.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    if (receitaData != null) {
                        val receitaEntity = receitaData.toReceitaEntity()
                        receitasFirebase.add(receitaEntity)
                    }
                }

                // Atualizar Room em background
                if (receitasFirebase.isNotEmpty()) {
                    // Executar em uma coroutine separada
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        try {
                            receitaDao.insertReceitas(receitasFirebase)
                        } catch (e: Exception) {
                            // Log do erro mas não falhar a operação
                            println("Erro ao inserir receitas no Room: ${e.message}")
                        }
                    }
                }
            }
            
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                println("Erro ao escutar Firebase: ${error.message}")
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
            
            // Deletar do Room primeiro
            receitaDao.deleteReceitaById(id)

            // Se online, deletar do Firebase
            if (connectivityObserver.isConnected()) {
                try {
                    db.child(id).removeValue().await()
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
    return ReceitaEntity(
        id = this["id"] as? String ?: "",
        nome = this["nome"] as? String ?: "",
        descricaoCurta = this["descricaoCurta"] as? String ?: "",
        imagemUrl = this["imagemUrl"] as? String ?: "",
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