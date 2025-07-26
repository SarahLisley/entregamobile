package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_cache")
data class NutritionCacheEntity(
    @PrimaryKey
    val recipeTitle: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val fiber: Double?,
    val sugar: Double?,
    val timestamp: Long = System.currentTimeMillis()
) 