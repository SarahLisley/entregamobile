package com.example.myapplication.core.data

import android.util.Log
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.database.entity.NutritionDataEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirebaseSyncService {
    private val database: FirebaseDatabase = Firebase.database
    private val receitasRef = database.getReference("receitas")
    private val nutritionRef = database.getReference("nutrition_data")

    suspend fun syncReceita(receita: ReceitaEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("FirebaseSync", "Sincronizando receita: ${receita.id}")
            Log.d("FirebaseSync", "Nome da receita: ${receita.nome}")
            Log.d("FirebaseSync", "URL da imagem: ${receita.imagemUrl}")
            val receitaData = mapOf(
                "id" to receita.id,
                "nome" to receita.nome,
                "descricaoCurta" to receita.descricaoCurta,
                "imagemUrl" to receita.imagemUrl,
                "ingredientes" to receita.ingredientes,
                "modoPreparo" to receita.modoPreparo,
                "tempoPreparo" to receita.tempoPreparo,
                "porcoes" to receita.porcoes,
                "userId" to receita.userId,
                "userEmail" to receita.userEmail,
                "curtidas" to receita.curtidas,
                "favoritos" to receita.favoritos,
                "tags" to receita.tags,
                "lastModified" to receita.lastModified,
                "isSynced" to true
            )
            receitasRef.child(receita.id).setValue(receitaData).await()
            Log.d("FirebaseSync", "Receita sincronizada com sucesso: ${receita.id}")
            Log.d("FirebaseSync", "Dados enviados para Firebase: $receitaData")
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Erro ao sincronizar receita: ${e.message}")
            return@withContext false
        }
    }

    suspend fun syncNutritionData(nutritionData: NutritionDataEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("FirebaseSync", "Sincronizando dados nutricionais: ${nutritionData.id}")
            val nutritionDataMap = mapOf(
                "id" to nutritionData.id,
                "receitaId" to nutritionData.receitaId,
                "calories" to nutritionData.calories,
                "protein" to nutritionData.protein,
                "fat" to nutritionData.fat,
                "carbohydrates" to nutritionData.carbohydrates,
                "fiber" to nutritionData.fiber,
                "sugar" to nutritionData.sugar,
                "createdAt" to nutritionData.createdAt,
                "isSynced" to true
            )
            nutritionRef.child(nutritionData.id).setValue(nutritionDataMap).await()
            Log.d("FirebaseSync", "Dados nutricionais sincronizados com sucesso: ${nutritionData.id}")
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Erro ao sincronizar dados nutricionais: ${e.message}")
            return@withContext false
        }
    }

    suspend fun deleteReceita(receitaId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("FirebaseSync", "Deletando receita do Firebase: $receitaId")
            receitasRef.child(receitaId).removeValue().await()
            Log.d("FirebaseSync", "Receita deletada com sucesso: $receitaId")
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Erro ao deletar receita: ${e.message}")
            return@withContext false
        }
    }

    suspend fun deleteNutritionData(nutritionDataId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("FirebaseSync", "Deletando dados nutricionais do Firebase: $nutritionDataId")
            nutritionRef.child(nutritionDataId).removeValue().await()
            Log.d("FirebaseSync", "Dados nutricionais deletados com sucesso: $nutritionDataId")
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Erro ao deletar dados nutricionais: ${e.message}")
            return@withContext false
        }
    }

    suspend fun updateReceita(receita: ReceitaEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("FirebaseSync", "Atualizando receita no Firebase: ${receita.id}")
            val updates = mapOf(
                "nome" to receita.nome,
                "descricaoCurta" to receita.descricaoCurta,
                "imagemUrl" to receita.imagemUrl,
                "ingredientes" to receita.ingredientes,
                "modoPreparo" to receita.modoPreparo,
                "tempoPreparo" to receita.tempoPreparo,
                "porcoes" to receita.porcoes,
                "curtidas" to receita.curtidas,
                "favoritos" to receita.favoritos,
                "tags" to receita.tags,
                "lastModified" to receita.lastModified
            )
            receitasRef.child(receita.id).updateChildren(updates).await()
            Log.d("FirebaseSync", "Receita atualizada com sucesso: ${receita.id}")
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Erro ao atualizar receita: ${e.message}")
            return@withContext false
        }
    }
} 