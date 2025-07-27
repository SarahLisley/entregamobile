package com.example.myapplication.data

import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.model.ChatMessage
import com.example.myapplication.core.data.network.NutritionService
import com.example.myapplication.core.data.network.ChatService

class GeminiNutritionService : NutritionService {
    override suspend fun getNutritionalInfo(recipe: ReceitaEntity): RecipeNutrition {
        return GeminiService.getNutritionalInfo(recipe)
    }
} 