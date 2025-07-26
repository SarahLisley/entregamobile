package com.example.myapplication.data

import androidx.room.*
import com.example.myapplication.model.NutritionCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionCacheDao {
    @Query("SELECT * FROM nutrition_cache WHERE recipeTitle = :recipeTitle")
    suspend fun getNutritionByTitle(recipeTitle: String): NutritionCacheEntity?
    
    @Query("SELECT * FROM nutrition_cache WHERE timestamp > :timestamp")
    suspend fun getRecentCache(timestamp: Long): List<NutritionCacheEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrition(nutrition: NutritionCacheEntity)
    
    @Delete
    suspend fun deleteNutrition(nutrition: NutritionCacheEntity)
    
    @Query("DELETE FROM nutrition_cache WHERE timestamp < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM nutrition_cache")
    suspend fun getCacheSize(): Int
} 