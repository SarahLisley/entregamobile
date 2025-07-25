package com.example.myapplication.data

import com.example.myapplication.model.NutritionResponse
import com.example.myapplication.model.RecipeNutrition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NutritionRepository {
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val apiService = retrofit.create(NutritionApiService::class.java)
    
    suspend fun getNutritionInfo(recipeTitle: String): Result<RecipeNutrition> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNutritionInfo(recipeTitle)
                
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
                        
                        Result.success(
                            RecipeNutrition(
                                calories = calories,
                                protein = protein,
                                fat = fat,
                                carbohydrates = carbs,
                                fiber = fiber,
                                sugar = sugar
                            )
                        )
                    } else {
                        Result.failure(Exception("Informações nutricionais não encontradas"))
                    }
                } else {
                    Result.failure(Exception("Receita não encontrada"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun searchRecipeNutrition(recipeTitle: String): Result<RecipeNutrition> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchRecipes(recipeTitle)
                
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
                        
                        Result.success(
                            RecipeNutrition(
                                calories = calories,
                                protein = protein,
                                fat = fat,
                                carbohydrates = carbs,
                                fiber = fiber,
                                sugar = sugar
                            )
                        )
                    } else {
                        Result.failure(Exception("Informações nutricionais não encontradas"))
                    }
                } else {
                    Result.failure(Exception("Receita não encontrada"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 