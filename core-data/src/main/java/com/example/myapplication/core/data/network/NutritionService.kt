package com.example.myapplication.core.data.network

import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.RecipeNutrition

interface NutritionService {
    suspend fun getNutritionalInfo(recipe: ReceitaEntity): RecipeNutrition
} 