package com.example.myapplication.ui.screens

import com.example.myapplication.core.data.database.entity.ReceitaEntity

// Extensão para converter ReceitaEntity para Map
fun ReceitaEntity.toMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "nome" to nome,
        "descricaoCurta" to descricaoCurta,
        "imagemUrl" to imagemUrl,
        "ingredientes" to ingredientes,
        "modoPreparo" to modoPreparo,
        "tempoPreparo" to tempoPreparo,
        "porcoes" to porcoes,
        "userId" to userId,
        "userEmail" to userEmail,
        "curtidas" to curtidas,
        "favoritos" to favoritos
    )
} 