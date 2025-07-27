package com.example.myapplication.data

import com.example.myapplication.core.data.network.ChatService

class GeminiChatService : ChatService {
    override suspend fun continueChat(history: List<com.example.myapplication.core.data.model.ChatMessage>, newMessage: String): String {
        return GeminiService.continueChat(history, newMessage)
    }

    override suspend fun generateRecipeFromConversation(history: List<com.example.myapplication.core.data.model.ChatMessage>): com.example.myapplication.core.data.database.entity.ReceitaEntity {
        return GeminiService.generateRecipeFromConversation(history)
    }
    
    override suspend fun generateImageForRecipe(recipe: com.example.myapplication.core.data.database.entity.ReceitaEntity): com.example.myapplication.core.data.database.entity.ReceitaEntity {
        return GeminiService.generateImageForRecipe(recipe)
    }
} 