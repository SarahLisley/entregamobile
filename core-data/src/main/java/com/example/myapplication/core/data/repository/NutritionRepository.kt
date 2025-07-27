package com.example.myapplication.core.data.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.database.entity.NutritionCacheEntity
import com.example.myapplication.core.data.database.entity.NutritionDataEntity
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.network.NutritionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NutritionRepository(
    private val context: Context,
    private val nutritionService: NutritionService
) {
    
    private val database = com.example.myapplication.core.data.database.AppDatabase.getDatabase(context)
    private val nutritionCacheDao = database.nutritionCacheDao()
    private val nutritionDataDao = database.nutritionDataDao()
    
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
                
                // Se não está no cache ou expirou, buscar na API Gemini
                val result = searchFromGeminiAPI(recipeTitle)
                
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

    suspend fun getNutritionInfoForRecipe(receita: ReceitaEntity): Result<RecipeNutrition> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("NutritionAPI", "Buscando informações nutricionais para receita: ${receita.id}")
                
                // Primeiro, verificar se já temos dados salvos para esta receita
                val savedNutrition = nutritionDataDao.getNutritionDataByReceitaId(receita.id)
                if (savedNutrition != null) {
                    Log.d("NutritionAPI", "Dados nutricionais encontrados no banco para receita: ${receita.id}")
                    return@withContext Result.success(
                        RecipeNutrition(
                            calories = savedNutrition.calories,
                            protein = savedNutrition.protein,
                            fat = savedNutrition.fat,
                            carbohydrates = savedNutrition.carbohydrates,
                            fiber = savedNutrition.fiber,
                            sugar = savedNutrition.sugar
                        )
                    )
                }
                
                // Se não tem dados salvos, buscar na API Gemini
                val result = searchFromGeminiAPIForRecipe(receita)
                
                // Se encontrou na API, salvar no banco de dados
                if (result.isSuccess) {
                    val nutrition = result.getOrNull()
                    if (nutrition != null) {
                        val nutritionEntity = NutritionDataEntity(
                            id = "${receita.id}_nutrition_${System.currentTimeMillis()}",
                            receitaId = receita.id,
                            calories = nutrition.calories,
                            protein = nutrition.protein,
                            fat = nutrition.fat,
                            carbohydrates = nutrition.carbohydrates,
                            fiber = nutrition.fiber,
                            sugar = nutrition.sugar,
                            isSynced = false
                        )
                        nutritionDataDao.insertNutritionData(nutritionEntity)
                        Log.d("NutritionAPI", "Dados nutricionais salvos no banco para receita: ${receita.id}")
                    }
                }
                
                result
            } catch (e: Exception) {
                Log.e("NutritionAPI", "Erro ao buscar informações nutricionais para receita: ${e.message}")
                Result.failure(e)
            }
        }
    }
    
    private suspend fun searchFromGeminiAPI(recipeTitle: String): Result<RecipeNutrition> {
        try {
            Log.d("NutritionAPI", "Buscando na API Gemini: $recipeTitle")
            
            // Criar uma receita temporária para análise
            val tempRecipe = ReceitaEntity(
                id = "temp",
                nome = recipeTitle,
                descricaoCurta = "",
                imagemUrl = "",
                ingredientes = listOf(recipeTitle), // Usar o título como ingrediente principal
                modoPreparo = emptyList(),
                tempoPreparo = "",
                porcoes = 1,
                userId = "",
                userEmail = null,
                curtidas = emptyList(),
                favoritos = emptyList()
            )
            
            val nutrition = nutritionService.getNutritionalInfo(tempRecipe)
            Log.d("NutritionAPI", "Informações nutricionais obtidas via Gemini")
            return Result.success(nutrition)
            
        } catch (e: Exception) {
            Log.e("NutritionAPI", "Erro na busca da API Gemini: ${e.message}")
            return Result.failure(e)
        }
    }

    private suspend fun searchFromGeminiAPIForRecipe(receita: ReceitaEntity): Result<RecipeNutrition> {
        try {
            Log.d("NutritionAPI", "Buscando na API Gemini para receita: ${receita.nome}")
            
            val nutrition = nutritionService.getNutritionalInfo(receita)
            Log.d("NutritionAPI", "Informações nutricionais obtidas via Gemini para receita: ${receita.id}")
            return Result.success(nutrition)
            
        } catch (e: Exception) {
            Log.e("NutritionAPI", "Erro na busca da API Gemini para receita: ${e.message}")
            return Result.failure(e)
        }
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

    // Função para obter dados nutricionais salvos de uma receita
    suspend fun getSavedNutritionData(receitaId: String): NutritionDataEntity? {
        return nutritionDataDao.getNutritionDataByReceitaId(receitaId)
    }
} 