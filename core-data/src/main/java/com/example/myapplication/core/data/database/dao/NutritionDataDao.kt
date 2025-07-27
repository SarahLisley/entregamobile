package com.example.myapplication.core.data.database.dao

import androidx.room.*
import com.example.myapplication.core.data.database.entity.NutritionDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDataDao {
    @Query("SELECT * FROM nutrition_data WHERE receitaId = :receitaId")
    suspend fun getNutritionDataByReceitaId(receitaId: String): NutritionDataEntity?

    @Query("SELECT * FROM nutrition_data WHERE isSynced = 0")
    suspend fun getUnsyncedNutritionData(): List<NutritionDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionData(nutritionData: NutritionDataEntity)

    @Update
    suspend fun updateNutritionData(nutritionData: NutritionDataEntity)

    @Delete
    suspend fun deleteNutritionData(nutritionData: NutritionDataEntity)

    @Query("DELETE FROM nutrition_data WHERE receitaId = :receitaId")
    suspend fun deleteNutritionDataByReceitaId(receitaId: String)

    @Query("UPDATE nutrition_data SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
} 