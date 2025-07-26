package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.myapplication.data.Converters

@Entity(tableName = "receitas")
@TypeConverters(Converters::class)
data class ReceitaEntity(
    @PrimaryKey
    val id: String,
    val nome: String,
    val descricaoCurta: String,
    val imagemUrl: String,
    val ingredientes: List<String>,
    val modoPreparo: List<String>,
    val tempoPreparo: String,
    val porcoes: Int,
    val userId: String,
    val userEmail: String?,
    val curtidas: List<String>,
    val favoritos: List<String>,
    val isSynced: Boolean = true,
    val lastModified: Long = System.currentTimeMillis()
) 