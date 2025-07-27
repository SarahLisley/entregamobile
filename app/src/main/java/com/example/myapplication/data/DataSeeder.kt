package com.example.myapplication.data

import android.content.Context
import android.util.Log
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.data.repository.NutritionRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSeeder @Inject constructor(
    private val context: Context,
    private val receitasRepository: ReceitasRepository,
    private val nutritionRepository: NutritionRepository
) {
    
    suspend fun seedDatabaseIfNeeded(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences("app_data", Context.MODE_PRIVATE)
                val isPopulated = prefs.getBoolean("database_populated", false)
                
                if (!isPopulated) {
                    Log.d("DataSeeder", "Carregando receitas do Firebase...")
                    val success = loadRecipesFromFirebase()
                    
                    if (success) {
                        prefs.edit().putBoolean("database_populated", true).apply()
                        Log.d("DataSeeder", "Receitas carregadas do Firebase com sucesso!")
                    }
                    
                    success
                } else {
                    Log.d("DataSeeder", "Banco de dados já foi populado anteriormente")
                    true
                }
            } catch (e: Exception) {
                Log.e("DataSeeder", "Erro ao carregar receitas: ${e.message}")
                false
            }
        }
    }
    
    suspend fun forcePopulateDatabase(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("DataSeeder", "Forçando carregamento de receitas do Firebase...")
                val success = loadRecipesFromFirebase()
                
                if (success) {
                    context.getSharedPreferences("app_data", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("database_populated", true)
                        .apply()
                    Log.d("DataSeeder", "Receitas carregadas do Firebase com sucesso!")
                }
                
                success
            } catch (e: Exception) {
                Log.e("DataSeeder", "Erro ao carregar receitas: ${e.message}")
                false
            }
        }
    }
    
    private suspend fun loadRecipesFromFirebase(): Boolean {
        return try {
            val database = FirebaseDatabase.getInstance()
            val receitasRef = database.getReference("receitas")
            
            Log.d("DataSeeder", "Conectando ao Firebase...")
            val snapshot = receitasRef.get().await()
            
            if (snapshot.exists()) {
                val receitasData = snapshot.getValue(object : GenericTypeIndicator<Map<String, Map<String, Any>>>() {})
                
                if (receitasData != null) {
                    var successCount = 0
                    
                    for ((id, receitaData) in receitasData) {
                        try {
                            val receita = createReceitaFromFirebaseData(id, receitaData)
                            receitasRepository.addReceita(receita)
                            successCount++
                            Log.d("DataSeeder", "Receita carregada do Firebase: ${receita.nome}")
                        } catch (e: Exception) {
                            Log.e("DataSeeder", "Erro ao carregar receita $id: ${e.message}")
                        }
                    }
                    
                    Log.d("DataSeeder", "Total de receitas carregadas do Firebase: $successCount")
                    successCount > 0
                } else {
                    Log.d("DataSeeder", "Nenhuma receita encontrada no Firebase")
                    true // Não é erro se não há receitas
                }
            } else {
                Log.d("DataSeeder", "Nenhuma receita encontrada no Firebase")
                true // Não é erro se não há receitas
            }
        } catch (e: Exception) {
            Log.e("DataSeeder", "Erro ao carregar receitas do Firebase: ${e.message}")
            false
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
    
    suspend fun importPopularRecipesFromAPI(titles: List<String>): Int {
        return withContext(Dispatchers.IO) {
            var successCount = 0
            
            for (title in titles) {
                try {
                    // Buscar informações nutricionais da API
                    val nutritionResult = nutritionRepository.getNutritionInfo(title)
                    
                    if (nutritionResult.isSuccess) {
                        val nutrition = nutritionResult.getOrNull()
                        
                        // Criar receita com dados da API + dados mockados
                        val recipe = createRecipeFromAPI(title, nutrition)
                        
                        // Salvar no banco
                        receitasRepository.salvarReceita(
                            context = context,
                            id = recipe.id,
                            nome = recipe.nome,
                            descricaoCurta = recipe.descricaoCurta,
                            imagemUri = null,
                            ingredientes = recipe.ingredientes,
                            modoPreparo = recipe.modoPreparo,
                            tempoPreparo = recipe.tempoPreparo,
                            porcoes = recipe.porcoes,
                            userId = recipe.userId,
                            userEmail = recipe.userEmail,
                            imagemUrl = recipe.imagemUrl
                        )
                        
                        successCount++
                        Log.d("DataSeeder", "Receita importada da API: $title")
                    }
                } catch (e: Exception) {
                    Log.e("DataSeeder", "Erro ao importar receita $title: ${e.message}")
                }
            }
            
            successCount
        }
    }
    
    private fun createRecipeFromAPI(title: String, nutrition: RecipeNutrition?): PredefinedRecipe {
        // Gerar dados mockados para complementar as informações da API
        val mockData = getMockDataForRecipe(title)
        
        return PredefinedRecipe(
            id = "api_${System.currentTimeMillis()}",
            nome = title,
            descricaoCurta = mockData.description,
            imagemUrl = mockData.imageUrl,
            ingredientes = mockData.ingredients,
            modoPreparo = mockData.instructions,
            tempoPreparo = mockData.cookingTime,
            porcoes = mockData.servings,
            userId = "admin",
            userEmail = "admin@admin.com",
            curtidas = emptyList(),
            favoritos = emptyList()
        )
    }
    
    private fun getMockDataForRecipe(title: String): MockRecipeData {
        return when (title.lowercase()) {
            "carrot cake" -> MockRecipeData(
                description = "Delicioso bolo de cenoura com cobertura cremosa",
                imageUrl = "https://images.unsplash.com/photo-1606788075761-8b8e3939b4cd?w=400&h=300&fit=crop",
                ingredients = listOf("Cenouras", "Farinha", "Açúcar", "Ovos", "Óleo", "Fermento"),
                instructions = listOf("Misture os ingredientes", "Asse por 40 minutos", "Deixe esfriar"),
                cookingTime = "50 min",
                servings = 8
            )
            "chocolate cake" -> MockRecipeData(
                description = "Bolo de chocolate fofinho e saboroso",
                imageUrl = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=300&fit=crop",
                ingredients = listOf("Chocolate", "Farinha", "Açúcar", "Ovos", "Leite", "Fermento"),
                instructions = listOf("Misture os ingredientes", "Asse por 45 minutos", "Deixe esfriar"),
                cookingTime = "55 min",
                servings = 8
            )
            else -> MockRecipeData(
                description = "Receita deliciosa e nutritiva",
                imageUrl = "https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=400&h=300&fit=crop",
                ingredients = listOf("Ingrediente 1", "Ingrediente 2", "Ingrediente 3"),
                instructions = listOf("Passo 1", "Passo 2", "Passo 3"),
                cookingTime = "30 min",
                servings = 4
            )
        }
    }
    
    data class PredefinedRecipe(
        val id: String,
        val nome: String,
        val descricaoCurta: String,
        val imagemUrl: String,
        val ingredientes: List<String>,
        val modoPreparo: List<String>,
        val tempoPreparo: String,
        val porcoes: Int,
        val userId: String,
        val userEmail: String,
        val curtidas: List<String>,
        val favoritos: List<String>
    )
    
    data class MockRecipeData(
        val description: String,
        val imageUrl: String,
        val ingredients: List<String>,
        val instructions: List<String>,
        val cookingTime: String,
        val servings: Int
    )
} 