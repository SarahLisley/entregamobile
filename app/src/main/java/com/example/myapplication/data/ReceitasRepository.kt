package com.example.myapplication.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import com.example.myapplication.model.ReceitaEntity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReceitasRepository(
    private val receitaDao: ReceitaDao,
    private val connectivityObserver: ConnectivityObserver
) {
    private val db = FirebaseDatabase.getInstance().getReference("receitas")
    private val storage = FirebaseStorage.getInstance().getReference("receita_images")

    // Função principal para salvar receita com verificação de conectividade
    suspend fun salvarReceita(
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
    ): String? {
        var finalImageUrl = ""
        
        // Upload da imagem se fornecida
        if (imagemUri != null) {
            val imageRef = storage.child("${id}_${System.currentTimeMillis()}")
            val uploadTask = imageRef.putFile(imagemUri).await()
            finalImageUrl = imageRef.downloadUrl.await().toString()
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
            } catch (e: Exception) {
                // Se falhar, marcar como não sincronizada
                receitaDao.updateReceita(receitaEntity.copy(isSynced = false))
            }
        }

        return finalImageUrl
    }

    // Função para salvar diretamente no Firebase (usada pelo SyncWorker)
    suspend fun salvarReceitaNoFirebase(receitaMap: Map<String, Any?>) {
        val id = receitaMap["id"] as String
        db.child(id).setValue(receitaMap).await()
    }

    // Função para obter receitas do Room (fonte única da verdade)
    fun getReceitas(): Flow<List<ReceitaEntity>> {
        return receitaDao.getAllReceitas()
    }

    // Função para sincronizar dados do Firebase com o Room
    suspend fun sincronizarComFirebase() {
        if (!connectivityObserver.isConnected()) return

        try {
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
        } catch (e: Exception) {
            println("Erro ao sincronizar com Firebase: ${e.message}")
        }
    }

    // Função para escutar mudanças do Firebase e atualizar o Room
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
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    if (receitasFirebase.isNotEmpty()) {
                        receitaDao.insertReceitas(receitasFirebase)
                    }
                }
            }
            
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                println("Erro ao escutar Firebase: ${error.message}")
            }
        })
    }

    // Função para atualizar curtidas
    suspend fun atualizarCurtidas(id: String, curtidas: List<String>) {
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
    }

    // Função para atualizar favoritos
    suspend fun atualizarFavoritos(id: String, favoritos: List<String>) {
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
    }

    // Função para deletar receita
    suspend fun deletarReceita(id: String) {
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
    }

    // Função para obter receitas não sincronizadas
    suspend fun getUnsyncedReceitas(): List<ReceitaEntity> {
        return receitaDao.getUnsyncedReceitas()
    }

    // Função para marcar receita como sincronizada
    suspend fun markAsSynced(receitaId: String) {
        receitaDao.markAsSynced(receitaId)
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