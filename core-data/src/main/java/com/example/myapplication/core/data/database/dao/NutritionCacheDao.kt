package com.example.myapplication.core.data.database.dao

import androidx.room.*
import com.example.myapplication.core.data.database.entity.NutritionCacheEntity

@Dao
interface NutritionCacheDao {
    
    @Query("SELECT * FROM nutrition_cache WHERE recipeTitle = :recipeTitle")
    suspend fun getNutritionByTitle(recipeTitle: String): NutritionCacheEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrition(nutrition: NutritionCacheEntity)
    
    @Delete
    suspend fun deleteNutrition(nutrition: NutritionCacheEntity)
    
    @Query("DELETE FROM nutrition_cache WHERE timestamp < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM nutrition_cache")
    suspend fun getCacheSize(): Int
} 