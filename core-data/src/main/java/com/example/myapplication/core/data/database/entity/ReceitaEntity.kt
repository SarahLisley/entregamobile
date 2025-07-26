package com.example.myapplication.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receitas")
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
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
) 