package com.example.myapplication.data

import androidx.room.*
import com.example.myapplication.model.ProdutoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produtos")
    fun getAll(): Flow<List<ProdutoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(produto: ProdutoEntity)

    @Query("SELECT * FROM produtos WHERE sincronizado = 0")
    suspend fun getPendentes(): List<ProdutoEntity>

    @Update
    suspend fun update(produto: ProdutoEntity)
}
