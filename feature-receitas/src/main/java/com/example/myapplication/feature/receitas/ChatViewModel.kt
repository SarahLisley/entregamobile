package com.example.myapplication.feature.receitas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.ChatMessage
import com.example.myapplication.core.data.network.ChatService
import com.example.myapplication.core.data.repository.AuthRepository
import com.example.myapplication.core.data.repository.ReceitasRepository
// import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
// import javax.inject.Inject

// Contexto do chat
data class ChatContext(
    val userPreferences: MutableSet<String> = mutableSetOf(),
    val dietaryRestrictions: MutableSet<String> = mutableSetOf(),
    var cookingLevel: String = "intermediate",
    val availableIngredients: MutableSet<String> = mutableSetOf(),
    val cuisinePreferences: MutableSet<String> = mutableSetOf(),
    var mealType: String? = null,
    var timeConstraints: String? = null
)

class ChatContextAnalyzer {
    fun analyzeContext(messages: List<ChatMessage>): ChatContext {
        val context = ChatContext()
        messages.forEach { message ->
            if (message.isUser) {
                analyzeUserMessage(message.content, context)
            }
        }
        return context
    }
    private fun analyzeUserMessage(content: String, context: ChatContext) {
        val lowerContent = content.lowercase()
        if (lowerContent.contains("vegetariano") || lowerContent.contains("vegetariana")) {
            context.dietaryRestrictions.add("vegetariano")
        }
        if (lowerContent.contains("vegano") || lowerContent.contains("vegana")) {
            context.dietaryRestrictions.add("vegano")
        }
        if (lowerContent.contains("sem glúten") || lowerContent.contains("celíaco")) {
            context.dietaryRestrictions.add("sem glúten")
        }
        if (lowerContent.contains("sem lactose") || lowerContent.contains("intolerante")) {
            context.dietaryRestrictions.add("sem lactose")
        }
        if (lowerContent.contains("iniciante") || lowerContent.contains("primeira vez")) {
            context.cookingLevel = "beginner"
        } else if (lowerContent.contains("experiente") || lowerContent.contains("chef")) {
            context.cookingLevel = "advanced"
        }
        if (lowerContent.contains("café da manhã") || lowerContent.contains("desjejum")) {
            context.mealType = "breakfast"
        } else if (lowerContent.contains("almoço")) {
            context.mealType = "lunch"
        } else if (lowerContent.contains("jantar")) {
            context.mealType = "dinner"
        } else if (lowerContent.contains("lanche") || lowerContent.contains("snack")) {
            context.mealType = "snack"
        }
        if (lowerContent.contains("rápido") || lowerContent.contains("menos de 30")) {
            context.timeConstraints = "quick"
        } else if (lowerContent.contains("demorado") || lowerContent.contains("lento")) {
            context.timeConstraints = "slow"
        }
    }
}

class ChatSuggestionEngine {
    fun generateSuggestions(context: ChatContext): List<String> {
        val suggestions = mutableListOf<String>()
        if (context.dietaryRestrictions.isEmpty()) {
            suggestions.add("Você tem alguma restrição alimentar? (vegetariano, vegano, sem glúten, etc.)")
        }
        if (context.cookingLevel == "intermediate") {
            suggestions.add("Qual é seu nível de experiência na cozinha?")
        }
        if (context.mealType == null) {
            suggestions.add("Para qual refeição você gostaria de uma receita? (café da manhã, almoço, jantar, lanche)")
        }
        if (context.timeConstraints == null) {
            suggestions.add("Quanto tempo você tem disponível para cozinhar?")
        }
        suggestions.add("Que tipo de culinária você prefere? (brasileira, italiana, asiática, etc.)")
        suggestions.add("Você tem ingredientes específicos que gostaria de usar?")
        suggestions.add("Posso criar uma receita personalizada para você!")
        return suggestions.take(3)
    }
}

class ChatViewModel(
    private val chatService: ChatService,
    private val receitasRepository: ReceitasRepository,
    private val authRepository: AuthRepository
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
    
    // Flag para controlar se a geração está em andamento
    private var isGenerating = false
    private var generationJob: Job? = null
    
    private val contextAnalyzer = ChatContextAnalyzer()
    private val suggestionEngine = ChatSuggestionEngine()

    private val _chatContext = MutableStateFlow(ChatContext())
    val chatContext: StateFlow<ChatContext> = _chatContext.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()
    
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
        
        // Atualizar contexto e sugestões
        updateChatContext()
        updateSuggestions()
        
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
        if (!_canGenerateRecipe.value || isGenerating) {
            Log.d("ChatViewModel", "🚫 Geração bloqueada - canGenerate: ${_canGenerateRecipe.value}, isGenerating: $isGenerating")
            return
        }
        
        Log.d("ChatViewModel", "🚀 INICIANDO GERAÇÃO DE RECEITA DO CHAT")
        isGenerating = true
        
        // Cancelar job anterior se existir
        generationJob?.cancel()
        
        generationJob = viewModelScope.launch {
            Log.d("ChatViewModel", "📋 Configurando estados iniciais...")
            _isLoading.value = true
            _recipeGenerationStatus.value = RecipeGenerationStatus.Generating
            _imageGenerationStatus.value = ImageGenerationStatus.Idle
            
            try {
                // Verificar se a coroutine ainda está ativa
                if (!isActive) {
                    Log.w("ChatViewModel", "⚠️ Coroutine foi cancelada antes de iniciar")
                    return@launch
                }
                
                // Gerar receita da conversa com informações do usuário logado
                val currentUserId = authRepository.currentUserId
                val currentUserEmail = authRepository.currentUserEmail
                Log.d("ChatViewModel", "👤 Usuário atual: $currentUserId")
                Log.d("ChatViewModel", "📧 Email do usuário: $currentUserEmail")
                Log.d("ChatViewModel", "💬 Mensagens no chat: ${_messages.value.size}")
                
                // Verificar se a coroutine ainda está ativa antes de gerar receita
                if (!isActive) {
                    Log.w("ChatViewModel", "⚠️ Coroutine foi cancelada antes de gerar receita")
                    return@launch
                }
                
                Log.d("ChatViewModel", "🔍 Iniciando geração de receita com timeout de 60s...")
                // Gerar receita com timeout de 60 segundos
                val recipe = withTimeout(60000L) {
                    chatService.generateRecipeFromConversation(
                        _messages.value,
                        currentUserId,
                        currentUserEmail
                    )
                }
                
                Log.d("ChatViewModel", "📝 Receita gerada: ${recipe.nome}")
                Log.d("ChatViewModel", "🆔 ID da receita: ${recipe.id}")
                Log.d("ChatViewModel", "📋 Ingredientes: ${recipe.ingredientes.size}")
                Log.d("ChatViewModel", "👨‍🍳 Modo de preparo: ${recipe.modoPreparo.size}")
                
                // Verificar se a coroutine ainda está ativa antes de gerar imagem
                if (!isActive) {
                    Log.w("ChatViewModel", "⚠️ Coroutine foi cancelada antes de gerar imagem")
                    return@launch
                }
                
                // Atualizar status para geração de imagem
                Log.d("ChatViewModel", "🎨 Atualizando status para geração de imagem...")
                _imageGenerationStatus.value = ImageGenerationStatus.Generating
                
                // Gerar imagem para a receita usando a nova API com timeout de 120 segundos
                Log.d("ChatViewModel", "🎨 Iniciando geração de imagem para receita: ${recipe.nome}")
                val recipeWithImage = withTimeout(120000L) {
                    chatService.generateImageForRecipe(recipe)
                }
                Log.d("ChatViewModel", "✅ Imagem gerada com sucesso para: ${recipeWithImage.nome}")
                Log.d("ChatViewModel", "🖼️ URL da imagem: ${recipeWithImage.imagemUrl}")
                
                // Verificar se a coroutine ainda está ativa antes de salvar
                if (!isActive) {
                    Log.w("ChatViewModel", "⚠️ Coroutine foi cancelada antes de salvar receita")
                    return@launch
                }
                
                // Salvar receita com imagem no repositório com timeout de 30 segundos
                Log.d("ChatViewModel", "💾 Salvando receita no repositório...")
                withTimeout(30000L) {
                    receitasRepository.addReceita(recipeWithImage)
                }
                Log.d("ChatViewModel", "✅ Receita salva com sucesso!")
                
                // Verificar se a coroutine ainda está ativa antes de finalizar
                if (!isActive) {
                    Log.w("ChatViewModel", "⚠️ Coroutine foi cancelada antes de finalizar")
                    return@launch
                }
                
                // Armazenar a receita gerada
                Log.d("ChatViewModel", "💾 Armazenando receita gerada...")
                _generatedRecipe.value = recipeWithImage
                
                // Adicionar mensagem informando que a receita foi gerada e salva
                Log.d("ChatViewModel", "💬 Adicionando mensagem de sucesso...")
                addMessage(
                    ChatMessage(
                        content = "🎉 Receita '${recipeWithImage.nome}' gerada com sucesso! A imagem única foi criada especialmente para você e a receita foi salva no seu feed.",
                        isUser = false
                    )
                )
                
                Log.d("ChatViewModel", "✅ Atualizando status de sucesso...")
                _recipeGenerationStatus.value = RecipeGenerationStatus.Success(recipeWithImage)
                _imageGenerationStatus.value = ImageGenerationStatus.Success
                
                Log.d("ChatViewModel", "🎉 GERAÇÃO DE RECEITA CONCLUÍDA COM SUCESSO!")
                
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> {
                        Log.w("ChatViewModel", "⚠️ Geração de receita foi cancelada pelo usuário")
                        Log.w("ChatViewModel", "⚠️ Motivo: ${e.message}")
                        
                        // Adicionar mensagem informando sobre o cancelamento
                        addMessage(
                            ChatMessage(
                                content = "Geração de receita foi cancelada. Você pode tentar novamente quando quiser.",
                                isUser = false
                            )
                        )
                        
                        _recipeGenerationStatus.value = RecipeGenerationStatus.Error("Geração cancelada pelo usuário")
                        _imageGenerationStatus.value = ImageGenerationStatus.Error("Geração cancelada")
                    }
                    is kotlinx.coroutines.TimeoutCancellationException -> {
                        Log.e("ChatViewModel", "⏰ Timeout na geração de receita")
                        Log.e("ChatViewModel", "⏰ Motivo: ${e.message}")
                        
                        addMessage(
                            ChatMessage(
                                content = "A geração de receita demorou muito tempo. Tente novamente ou continue nossa conversa para gerar uma receita mais simples.",
                                isUser = false
                            )
                        )
                        
                        _recipeGenerationStatus.value = RecipeGenerationStatus.Error("Timeout na geração")
                        _imageGenerationStatus.value = ImageGenerationStatus.Error("Timeout na geração de imagem")
                    }
                    else -> {
                        Log.e("ChatViewModel", "💥 ERRO na geração de receita")
                        Log.e("ChatViewModel", "💥 Tipo da exceção: ${e.javaClass.simpleName}")
                        Log.e("ChatViewModel", "💥 Mensagem: ${e.message}")
                        Log.e("ChatViewModel", "💥 Stack trace:")
                        e.printStackTrace()
                        
                        addMessage(
                            ChatMessage(
                                content = "Desculpe, não consegui gerar a receita. Tente continuar nossa conversa e tente novamente.",
                                isUser = false
                            )
                        )
                        _recipeGenerationStatus.value = RecipeGenerationStatus.Error(e.message ?: "Erro desconhecido")
                        _imageGenerationStatus.value = ImageGenerationStatus.Error(e.message ?: "Erro ao gerar imagem")
                    }
                }
            } finally {
                Log.d("ChatViewModel", "🏁 Finalizando geração de receita...")
                _isLoading.value = false
                isGenerating = false
                generationJob = null
                Log.d("ChatViewModel", "🏁 Finalizando geração de receita")
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

    private fun updateChatContext() {
        val newContext = contextAnalyzer.analyzeContext(_messages.value)
        _chatContext.value = newContext
    }

    private fun updateSuggestions() {
        val newSuggestions = suggestionEngine.generateSuggestions(_chatContext.value)
        _suggestions.value = newSuggestions
    }
    
    fun clearChat() {
        _messages.value = emptyList()
        _canGenerateRecipe.value = false
        _generatedRecipe.value = null
        _recipeGenerationStatus.value = RecipeGenerationStatus.Idle
        _imageGenerationStatus.value = ImageGenerationStatus.Idle
        isGenerating = false
        generationJob?.cancel()
        generationJob = null
        
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
        isGenerating = false
        generationJob?.cancel()
        generationJob = null
    }
    
    // Função para cancelar a geração de receita
    fun cancelRecipeGeneration() {
        if (isGenerating) {
            Log.d("ChatViewModel", "🛑 Cancelando geração de receita pelo usuário")
            isGenerating = false
            _isLoading.value = false
            _recipeGenerationStatus.value = RecipeGenerationStatus.Idle
            _imageGenerationStatus.value = ImageGenerationStatus.Idle
            generationJob?.cancel()
            generationJob = null
            
            addMessage(
                ChatMessage(
                    content = "Geração de receita cancelada. Você pode tentar novamente quando quiser.",
                    isUser = false
                )
            )
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        Log.d("ChatViewModel", "🧹 ViewModel sendo limpo")
        generationJob?.cancel()
        generationJob = null
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