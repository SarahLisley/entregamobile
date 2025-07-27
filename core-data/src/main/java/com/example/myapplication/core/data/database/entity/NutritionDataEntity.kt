package com.example.myapplication.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_data")
data class NutritionDataEntity(
    @PrimaryKey
    val id: String,
    val receitaId: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val fiber: Double?,
    val sugar: Double?,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) 