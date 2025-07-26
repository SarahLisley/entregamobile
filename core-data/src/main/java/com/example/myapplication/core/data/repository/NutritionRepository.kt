package com.example.myapplication.core.data.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.database.entity.NutritionCacheEntity
import com.example.myapplication.core.data.network.NutritionApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NutritionRepository(private val context: Context) {
    
    private val database = com.example.myapplication.core.data.database.AppDatabase.getDatabase(context)
    private val nutritionCacheDao = database.nutritionCacheDao()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val apiService = retrofit.create(NutritionApiService::class.java)
    
    suspend fun getNutritionInfo(recipeTitle: String): Result<RecipeNutrition> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("NutritionAPI", "Buscando informações para: $recipeTitle")
                
                // Primeiro, verificar se existe no cache
                val cachedNutrition = nutritionCacheDao.getNutritionByTitle(recipeTitle)
                if (cachedNutrition != null) {
                    val cacheAge = System.currentTimeMillis() - cachedNutrition.timestamp
                    val cacheValidFor = 7 * 24 * 60 * 60 * 1000L // 7 dias em millisegundos
                    
                    if (cacheAge < cacheValidFor) {
                        Log.d("NutritionAPI", "Informações encontradas no cache")
                        return@withContext Result.success(
                            RecipeNutrition(
                                calories = cachedNutrition.calories,
                                protein = cachedNutrition.protein,
                                fat = cachedNutrition.fat,
                                carbohydrates = cachedNutrition.carbohydrates,
                                fiber = cachedNutrition.fiber,
                                sugar = cachedNutrition.sugar
                            )
                        )
                    } else {
                        Log.d("NutritionAPI", "Cache expirado, buscando na API")
                        nutritionCacheDao.deleteNutrition(cachedNutrition)
                    }
                }
                
                // Se não está no cache ou expirou, buscar na API
                val result = searchFromAPI(recipeTitle)
                
                // Se encontrou na API, salvar no cache
                if (result.isSuccess) {
                    val nutrition = result.getOrNull()
                    if (nutrition != null) {
                        val cacheEntity = NutritionCacheEntity(
                            recipeTitle = recipeTitle,
                            calories = nutrition.calories,
                            protein = nutrition.protein,
                            fat = nutrition.fat,
                            carbohydrates = nutrition.carbohydrates,
                            fiber = nutrition.fiber,
                            sugar = nutrition.sugar
                        )
                        nutritionCacheDao.insertNutrition(cacheEntity)
                        Log.d("NutritionAPI", "Informações salvas no cache")
                    }
                }
                
                result
            } catch (e: Exception) {
                Log.e("NutritionAPI", "Erro geral: ${e.message}")
                Result.failure(e)
            }
        }
    }
    
    private suspend fun searchFromAPI(recipeTitle: String): Result<RecipeNutrition> {
        try {
            Log.d("NutritionAPI", "Buscando na API: $recipeTitle")
            
            // Strategy 1: Try with original title
            val result = trySearchStrategies(recipeTitle)
            if (result.isSuccess) {
                return result
            }
            
            // Strategy 2: Try with English translation for common recipes
            val englishTitle = translateToEnglish(recipeTitle)
            if (englishTitle != recipeTitle) {
                Log.d("NutritionAPI", "Tentando com tradução: $englishTitle")
                val translatedResult = trySearchStrategies(englishTitle)
                if (translatedResult.isSuccess) {
                    return translatedResult
                }
            }
            
            // Strategy 3: Try with simplified title
            val simplifiedTitle = simplifyTitle(recipeTitle)
            if (simplifiedTitle != recipeTitle) {
                Log.d("NutritionAPI", "Tentando com título simplificado: $simplifiedTitle")
                val simplifiedResult = trySearchStrategies(simplifiedTitle)
                if (simplifiedResult.isSuccess) {
                    return simplifiedResult
                }
            }
            
            return result // Return the original failure
        } catch (e: Exception) {
            Log.e("NutritionAPI", "Erro na busca da API: ${e.message}")
            return Result.failure(e)
        }
    }
    
    private suspend fun trySearchStrategies(recipeTitle: String): Result<RecipeNutrition> {
        // Strategy 1: Try complexSearch first (better for recipes)
        try {
            Log.d("NutritionAPI", "Tentando complexSearch...")
            val response = apiService.searchRecipesWithNutrition(recipeTitle)
            Log.d("NutritionAPI", "Resposta complexSearch: ${response.results?.size ?: 0} resultados")
            
            if (response.results?.isNotEmpty() == true) {
                val nutrition = response.results.first().nutrition
                if (nutrition != null) {
                    val nutrients = nutrition.nutrients ?: emptyList()
                    
                    val calories = nutrition.calories ?: 0.0
                    val protein = nutrients.find { it.name?.contains("Protein", ignoreCase = true) == true }?.amount ?: 0.0
                    val fat = nutrients.find { it.name?.contains("Fat", ignoreCase = true) == true }?.amount ?: 0.0
                    val carbs = nutrients.find { it.name?.contains("Carbohydrates", ignoreCase = true) == true }?.amount ?: 0.0
                    val fiber = nutrients.find { it.name?.contains("Fiber", ignoreCase = true) == true }?.amount
                    val sugar = nutrients.find { it.name?.contains("Sugar", ignoreCase = true) == true }?.amount
                    
                    Log.d("NutritionAPI", "Informações nutricionais encontradas via complexSearch")
                    return Result.success(
                        RecipeNutrition(
                            calories = calories,
                            protein = protein,
                            fat = fat,
                            carbohydrates = carbs,
                            fiber = fiber,
                            sugar = sugar
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("NutritionAPI", "Erro no complexSearch: ${e.message}")
        }
        
        // Strategy 2: Try guessNutrition as fallback
        try {
            Log.d("NutritionAPI", "Tentando guessNutrition...")
            val response = apiService.getNutritionInfo(recipeTitle)
            Log.d("NutritionAPI", "Resposta guessNutrition: ${response.results?.size ?: 0} resultados")
            
            if (response.results?.isNotEmpty() == true) {
                val nutrition = response.results.first().nutrition
                if (nutrition != null) {
                    val nutrients = nutrition.nutrients ?: emptyList()
                    
                    val calories = nutrition.calories ?: 0.0
                    val protein = nutrients.find { it.name?.contains("Protein", ignoreCase = true) == true }?.amount ?: 0.0
                    val fat = nutrients.find { it.name?.contains("Fat", ignoreCase = true) == true }?.amount ?: 0.0
                    val carbs = nutrients.find { it.name?.contains("Carbohydrates", ignoreCase = true) == true }?.amount ?: 0.0
                    val fiber = nutrients.find { it.name?.contains("Fiber", ignoreCase = true) == true }?.amount
                    val sugar = nutrients.find { it.name?.contains("Sugar", ignoreCase = true) == true }?.amount
                    
                    Log.d("NutritionAPI", "Informações nutricionais encontradas via guessNutrition")
                    return Result.success(
                        RecipeNutrition(
                            calories = calories,
                            protein = protein,
                            fat = fat,
                            carbohydrates = carbs,
                            fiber = fiber,
                            sugar = sugar
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("NutritionAPI", "Erro no guessNutrition: ${e.message}")
        }
        
        Log.e("NutritionAPI", "Receita não encontrada")
        return Result.failure(Exception("Receita não encontrada"))
    }
    
    // Função para limpar cache antigo (pode ser chamada periodicamente)
    suspend fun cleanOldCache() {
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        nutritionCacheDao.deleteOldCache(oneWeekAgo)
        Log.d("NutritionAPI", "Cache antigo limpo")
    }
    
    // Função para obter estatísticas do cache
    suspend fun getCacheStats(): Int {
        return nutritionCacheDao.getCacheSize()
    }
    
    private fun translateToEnglish(recipeTitle: String): String {
        return when (recipeTitle.lowercase()) {
            "bolo de cenoura" -> "carrot cake"
            "bolo de chocolate" -> "chocolate cake"
            "bolo de banana" -> "banana bread"
            "bolo de laranja" -> "orange cake"
            "bolo de limão" -> "lemon cake"
            "bolo de fubá" -> "cornmeal cake"
            "bolo de milho" -> "corn cake"
            "bolo de coco" -> "coconut cake"
            "bolo de maçã" -> "apple cake"
            "bolo de abacaxi" -> "pineapple cake"
            "pão de queijo" -> "cheese bread"
            "brigadeiro" -> "chocolate truffle"
            "beijinho" -> "coconut truffle"
            "quindim" -> "coconut custard"
            "pudim" -> "pudding"
            "mousse de chocolate" -> "chocolate mousse"
            "mousse de maracujá" -> "passion fruit mousse"
            "torta de frango" -> "chicken pie"
            "torta de palmito" -> "hearts of palm pie"
            "torta de camarão" -> "shrimp pie"
            "feijoada" -> "black bean stew"
            "coxinha" -> "chicken croquette"
            "lasanha" -> "lasagna"
            "panqueca" -> "pancake"
            "tapioca" -> "tapioca"
            "omelete" -> "omelette"
            "omelete de queijo e tomate" -> "cheese tomato omelette"
            "tapioca com coco e leite condensado" -> "tapioca with coconut"
            "salada de grão-de-bico" -> "chickpea salad"
            else -> recipeTitle
        }
    }
    
    private fun simplifyTitle(recipeTitle: String): String {
        return recipeTitle.lowercase()
            .replace("bolo de ", "")
            .replace("torta de ", "")
            .replace("mousse de ", "")
            .replace("pão de ", "")
            .trim()
    }
} 