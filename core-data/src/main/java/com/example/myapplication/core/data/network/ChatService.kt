package com.example.myapplication.core.data.network

import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.ChatMessage

interface ChatService {
    suspend fun continueChat(history: List<ChatMessage>, newMessage: String): String
    suspend fun generateRecipeFromConversation(history: List<ChatMessage>): ReceitaEntity
    suspend fun generateImageForRecipe(recipe: ReceitaEntity): ReceitaEntity
} 