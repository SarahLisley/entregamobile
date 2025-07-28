package com.example.myapplication.core.data.database.dao

import androidx.room.*
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceitaDao {
    
    @Query("SELECT * FROM receitas ORDER BY lastModified DESC")
    fun getAllReceitas(): Flow<List<ReceitaEntity>>
    
    @Query("SELECT * FROM receitas WHERE id = :id")
    suspend fun getReceitaById(id: String): ReceitaEntity?
    
    @Query("SELECT * FROM receitas WHERE isSynced = 0")
    suspend fun getUnsyncedReceitas(): List<ReceitaEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceita(receita: ReceitaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceitas(receitas: List<ReceitaEntity>)
    
    @Update
    suspend fun updateReceita(receita: ReceitaEntity)
    
    @Query("UPDATE receitas SET curtidas = :curtidas WHERE id = :id")
    suspend fun updateCurtidas(id: String, curtidas: List<String>)
    
    @Query("UPDATE receitas SET favoritos = :favoritos WHERE id = :id")
    suspend fun updateFavoritos(id: String, favoritos: List<String>)
    
    @Query("UPDATE receitas SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
    
    @Query("DELETE FROM receitas WHERE id = :id")
    suspend fun deleteReceitaById(id: String)
    
    @Query("DELETE FROM receitas")
    suspend fun deleteAllReceitas()
} 