package com.example.myapplication.data

import android.content.Context
import android.util.Log
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
                    Log.d("DataSeeder", "Populando banco de dados com receitas predefinidas...")
                    val success = populateWithPredefinedRecipes()
                    
                    if (success) {
                        prefs.edit().putBoolean("database_populated", true).apply()
                        Log.d("DataSeeder", "Banco de dados populado com sucesso!")
                    }
                    
                    success
                } else {
                    Log.d("DataSeeder", "Banco de dados já foi populado anteriormente")
                    true
                }
            } catch (e: Exception) {
                Log.e("DataSeeder", "Erro ao popular banco de dados: ${e.message}")
                false
            }
        }
    }
    
    suspend fun forcePopulateDatabase(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("DataSeeder", "Forçando população do banco de dados...")
                val success = populateWithPredefinedRecipes()
                
                if (success) {
                    context.getSharedPreferences("app_data", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("database_populated", true)
                        .apply()
                    Log.d("DataSeeder", "Banco de dados populado com sucesso!")
                }
                
                success
            } catch (e: Exception) {
                Log.e("DataSeeder", "Erro ao popular banco de dados: ${e.message}")
                false
            }
        }
    }
    
    private suspend fun populateWithPredefinedRecipes(): Boolean {
        return try {
            val recipes = loadPredefinedRecipes()
            var successCount = 0
            
            for (recipe in recipes) {
                try {
                    // Salvar a receita no banco
                    receitasRepository.salvarReceita(
                        context = context,
                        id = recipe.id,
                        nome = recipe.nome,
                        descricaoCurta = recipe.descricaoCurta,
                        imagemUri = null, // Usar a URL da imagem
                        ingredientes = recipe.ingredientes,
                        modoPreparo = recipe.modoPreparo,
                        tempoPreparo = recipe.tempoPreparo,
                        porcoes = recipe.porcoes,
                        userId = recipe.userId,
                        userEmail = recipe.userEmail,
                        imagemUrl = recipe.imagemUrl
                    )
                    successCount++
                    Log.d("DataSeeder", "Receita salva: ${recipe.nome}")
                } catch (e: Exception) {
                    Log.e("DataSeeder", "Erro ao salvar receita ${recipe.nome}: ${e.message}")
                }
            }
            
            Log.d("DataSeeder", "Total de receitas salvas: $successCount/${recipes.size}")
            successCount > 0
        } catch (e: Exception) {
            Log.e("DataSeeder", "Erro ao carregar receitas predefinidas: ${e.message}")
            false
        }
    }
    
    private fun loadPredefinedRecipes(): List<PredefinedRecipe> {
        return try {
            val json = context.assets.open("receitas_predefinidas.json")
                .bufferedReader()
                .use { it.readText() }
            
            val type = object : TypeToken<List<PredefinedRecipe>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e("DataSeeder", "Erro ao ler arquivo JSON: ${e.message}")
            emptyList()
        }
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
                instructions = listOf("Derreta o chocolate", "Misture os ingredientes", "Asse por 35 minutos"),
                cookingTime = "45 min",
                servings = 10
            )
            "black bean stew" -> MockRecipeData(
                description = "Prato tradicional brasileiro com feijão preto e carnes",
                imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400&h=300&fit=crop",
                ingredients = listOf("Feijão preto", "Carne seca", "Linguiça", "Paio", "Louro", "Sal"),
                instructions = listOf("Cozinhe o feijão com as carnes", "Adicione temperos", "Cozinhe por 2 horas"),
                cookingTime = "3 horas",
                servings = 8
            )
            "chocolate mousse" -> MockRecipeData(
                description = "Sobremesa cremosa e deliciosa de chocolate",
                imageUrl = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=300&fit=crop",
                ingredients = listOf("Chocolate", "Ovos", "Creme de leite", "Açúcar"),
                instructions = listOf("Derreta o chocolate", "Bata as claras", "Misture tudo", "Leve à geladeira"),
                cookingTime = "4 horas",
                servings = 6
            )
            "cheese bread" -> MockRecipeData(
                description = "Pão de queijo mineiro tradicional",
                imageUrl = "https://images.unsplash.com/photo-1608198093002-ad4e505484ba?w=400&h=300&fit=crop",
                ingredients = listOf("Polvilho", "Leite", "Óleo", "Ovos", "Queijo", "Sal"),
                instructions = listOf("Ferva o leite", "Misture com polvilho", "Faça bolinhas", "Asse"),
                cookingTime = "30 min",
                servings = 20
            )
            "chicken croquette" -> MockRecipeData(
                description = "Salgadinho brasileiro recheado com frango",
                imageUrl = "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=400&h=300&fit=crop",
                ingredients = listOf("Farinha", "Água", "Frango", "Cebola", "Manteiga"),
                instructions = listOf("Faça a massa", "Recheie com frango", "Passe na farinha", "Frite"),
                cookingTime = "1 hora",
                servings = 12
            )
            else -> MockRecipeData(
                description = "Receita deliciosa e tradicional",
                imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=300&fit=crop",
                ingredients = listOf("Ingrediente 1", "Ingrediente 2", "Ingrediente 3"),
                instructions = listOf("Passo 1", "Passo 2", "Passo 3"),
                cookingTime = "30 min",
                servings = 4
            )
        }
    }
    
    // Data classes para deserialização do JSON
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
    
    private data class MockRecipeData(
        val description: String,
        val imageUrl: String,
        val ingredients: List<String>,
        val instructions: List<String>,
        val cookingTime: String,
        val servings: Int
    )
} 