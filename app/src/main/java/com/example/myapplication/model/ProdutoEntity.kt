package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produtos")
data class ProdutoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val preco: Double,
    val sincronizado: Boolean = false
)
