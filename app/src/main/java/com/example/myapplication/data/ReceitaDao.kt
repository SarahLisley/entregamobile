package com.example.myapplication.data

import androidx.room.*
import com.example.myapplication.model.ReceitaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceitaDao {
    
    @Query("SELECT * FROM receitas ORDER BY lastModified DESC")
    fun getAllReceitas(): Flow<List<ReceitaEntity>>
    
    @Query("SELECT * FROM receitas WHERE isSynced = 0")
    suspend fun getUnsyncedReceitas(): List<ReceitaEntity>
    
    @Query("SELECT * FROM receitas WHERE id = :receitaId")
    suspend fun getReceitaById(receitaId: String): ReceitaEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceita(receita: ReceitaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceitas(receitas: List<ReceitaEntity>)
    
    @Update
    suspend fun updateReceita(receita: ReceitaEntity)
    
    @Delete
    suspend fun deleteReceita(receita: ReceitaEntity)
    
    @Query("DELETE FROM receitas WHERE id = :receitaId")
    suspend fun deleteReceitaById(receitaId: String)
    
    @Query("UPDATE receitas SET isSynced = 1 WHERE id = :receitaId")
    suspend fun markAsSynced(receitaId: String)
    
    @Query("UPDATE receitas SET curtidas = :curtidas WHERE id = :receitaId")
    suspend fun updateCurtidas(receitaId: String, curtidas: List<String>)
    
    @Query("UPDATE receitas SET favoritos = :favoritos WHERE id = :receitaId")
    suspend fun updateFavoritos(receitaId: String, favoritos: List<String>)
    
    @Query("DELETE FROM receitas")
    suspend fun deleteAllReceitas()
} 