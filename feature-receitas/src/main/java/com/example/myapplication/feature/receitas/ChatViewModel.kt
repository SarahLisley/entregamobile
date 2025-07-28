package com.example.myapplication.feature.receitas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.ChatMessage
import com.example.myapplication.core.data.network.ChatService
import com.example.myapplication.core.data.repository.ReceitasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatService: ChatService,
    private val receitasRepository: ReceitasRepository,
    private val currentUserId: String? = null,
    private val currentUserEmail: String? = null
) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _canGenerateRecipe = MutableStateFlow(false)
    val canGenerateRecipe: StateFlow<Boolean> = _canGenerateRecipe.asStateFlow()
    
    private val _generatedRecipe = MutableStateFlow<ReceitaEntity?>(null)
    val generatedRecipe: StateFlow<ReceitaEntity?> = _generatedRecipe.asStateFlow()
    
    private val _recipeGenerationStatus = MutableStateFlow<RecipeGenerationStatus>(RecipeGenerationStatus.Idle)
    val recipeGenerationStatus: StateFlow<RecipeGenerationStatus> = _recipeGenerationStatus.asStateFlow()
    
    // Novos estados para geração de imagens
    private val _imageGenerationStatus = MutableStateFlow<ImageGenerationStatus>(ImageGenerationStatus.Idle)
    val imageGenerationStatus: StateFlow<ImageGenerationStatus> = _imageGenerationStatus.asStateFlow()
    
    init {
        // Adicionar mensagem de boas-vindas
        addMessage(
            ChatMessage(
                content = "Olá! Sou o Chef Gemini, seu assistente culinário personalizado. Posso ajudar você com dicas de culinária, informações nutricionais e até mesmo criar receitas personalizadas com imagens únicas geradas por IA. Como posso ajudá-lo hoje?",
                isUser = false
            )
        )
    }
    
    fun sendMessage(content: String) {
        if (content.isBlank()) return
        
        // Adicionar mensagem do usuário
        addMessage(ChatMessage(content = content, isUser = true))
        
        // Verificar se pode gerar receita (após algumas interações)
        updateCanGenerateRecipe()
        
        // Obter resposta da IA
        getAIResponse(content)
    }
    
    private fun getAIResponse(userMessage: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = chatService.continueChat(_messages.value, userMessage)
                addMessage(ChatMessage(content = response, isUser = false))
            } catch (e: Exception) {
                addMessage(
                    ChatMessage(
                        content = "Desculpe, ocorreu um erro ao processar sua mensagem. Tente novamente.",
                        isUser = false
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun generateRecipeFromChat() {
        if (!_canGenerateRecipe.value) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _recipeGenerationStatus.value = RecipeGenerationStatus.Generating
            _imageGenerationStatus.value = ImageGenerationStatus.Idle
            
            try {
                // Gerar receita da conversa com informações do usuário logado
                val recipe = chatService.generateRecipeFromConversation(
                    _messages.value,
                    currentUserId,
                    currentUserEmail
                )
                
                // Atualizar status para geração de imagem
                _imageGenerationStatus.value = ImageGenerationStatus.Generating
                
                // Gerar imagem para a receita usando a nova API
                val recipeWithImage = chatService.generateImageForRecipe(recipe)
                
                // Salvar receita com imagem no repositório
                receitasRepository.addReceita(recipeWithImage)
                
                // Armazenar a receita gerada
                _generatedRecipe.value = recipeWithImage
                
                // Adicionar mensagem informando que a receita foi gerada e salva
                addMessage(
                    ChatMessage(
                        content = "🎉 Receita '${recipeWithImage.nome}' gerada com sucesso! A imagem única foi criada especialmente para você e a receita foi salva no seu feed.",
                        isUser = false
                    )
                )
                
                _recipeGenerationStatus.value = RecipeGenerationStatus.Success(recipeWithImage)
                _imageGenerationStatus.value = ImageGenerationStatus.Success
                
            } catch (e: Exception) {
                addMessage(
                    ChatMessage(
                        content = "Desculpe, não consegui gerar a receita. Tente continuar nossa conversa e tente novamente.",
                        isUser = false
                    )
                )
                _recipeGenerationStatus.value = RecipeGenerationStatus.Error(e.message ?: "Erro desconhecido")
                _imageGenerationStatus.value = ImageGenerationStatus.Error(e.message ?: "Erro ao gerar imagem")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun addMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }
    
    private fun updateCanGenerateRecipe() {
        // Pode gerar receita desde a primeira mensagem do usuário
        val userMessages = _messages.value.count { it.isUser }
        _canGenerateRecipe.value = userMessages >= 1
    }
    
    fun clearChat() {
        _messages.value = emptyList()
        _canGenerateRecipe.value = false
        _generatedRecipe.value = null
        _recipeGenerationStatus.value = RecipeGenerationStatus.Idle
        _imageGenerationStatus.value = ImageGenerationStatus.Idle
        
        // Adicionar mensagem de boas-vindas novamente
        addMessage(
            ChatMessage(
                content = "Chat limpo! Como posso ajudá-lo hoje?",
                isUser = false
            )
        )
    }
    
    fun resetRecipeGenerationStatus() {
        _recipeGenerationStatus.value = RecipeGenerationStatus.Idle
        _imageGenerationStatus.value = ImageGenerationStatus.Idle
    }
}

sealed class RecipeGenerationStatus {
    object Idle : RecipeGenerationStatus()
    object Generating : RecipeGenerationStatus()
    data class Success(val recipe: ReceitaEntity) : RecipeGenerationStatus()
    data class Error(val message: String) : RecipeGenerationStatus()
}

sealed class ImageGenerationStatus {
    object Idle : ImageGenerationStatus()
    object Generating : ImageGenerationStatus()
    object Success : ImageGenerationStatus()
    data class Error(val message: String) : ImageGenerationStatus()
} 