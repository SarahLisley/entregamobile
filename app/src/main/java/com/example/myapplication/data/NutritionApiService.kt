package com.example.myapplication.data

import com.example.myapplication.model.NutritionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NutritionApiService {
    
    @GET("recipes/guessNutrition")
    suspend fun getNutritionInfo(
        @Query("title") title: String,
        @Query("apiKey") apiKey: String = "14405c3dbc1c4bd0a92fe3f0b53a8d23" // Substitua pela sua chave
    ): NutritionResponse
    
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("addRecipeNutrition") addNutrition: Boolean = true,
        @Query("apiKey") apiKey: String = "YOUR_SPOONACULAR_API_KEY",
        @Query("number") number: Int = 1
    ): NutritionResponse
} 