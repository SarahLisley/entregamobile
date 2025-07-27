package com.example.myapplication.ui.screens

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.data.storage.ImageStorageService
import com.example.myapplication.core.data.repository.NutritionRepository
import com.example.myapplication.core.data.database.AppDatabase
import com.example.myapplication.core.data.network.ConnectivityObserver
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.SupabaseImageUploader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import com.example.myapplication.data.GeminiNutritionService

sealed class ReceitasUiState {
    object Loading : ReceitasUiState()
    data class Success(val receitas: List<ReceitaEntity>) : ReceitasUiState()
    data class Error(val message: String) : ReceitasUiState()
}

class ReceitasViewModel(
    private val app: Application
) : AndroidViewModel(app) {
    private val database = AppDatabase.getDatabase(app)
    private val receitaDao = database.receitaDao()
    private val connectivityObserver = ConnectivityObserver(app)
    private val repository = ReceitasRepository(
        receitaDao,
        database.nutritionDataDao(),
        connectivityObserver,
        ImageStorageService(),
        com.example.myapplication.core.ui.error.ErrorHandler()
    )
    private val nutritionRepository = NutritionRepository(app, GeminiNutritionService())
    
    private val _uiState = MutableStateFlow<ReceitasUiState>(ReceitasUiState.Loading)
    val uiState: StateFlow<ReceitasUiState> = _uiState

    // Channel para eventos únicos (ex: Snackbars)
    private val _eventChannel = Channel<String>()
    val eventFlow = _eventChannel.receiveAsFlow()
    
    // Estado para informações nutricionais
    private val _nutritionState = MutableStateFlow<RecipeNutrition?>(null)
    val nutritionState: StateFlow<RecipeNutrition?> = _nutritionState
    
    // Estado para receitas recomendadas
    private val _recommendedRecipes = MutableStateFlow<List<ReceitaEntity>>(emptyList())
    val recommendedRecipes: StateFlow<List<ReceitaEntity>> = _recommendedRecipes

    init {
        carregarReceitas()
        sincronizarComFirebase()
    }

    fun carregarReceitas() {
        viewModelScope.launch {
            try {
                // Observar receitas do Room (fonte única da verdade)
                repository.getReceitas().collect { receitas ->
                    _uiState.value = ReceitasUiState.Success(receitas)
                    // Carregar recomendações após as receitas serem carregadas
                    carregarReceitasRecomendadas(receitas)
                }
            } catch (e: Exception) {
                _uiState.value = ReceitasUiState.Error(e.message ?: "Erro ao carregar receitas")
            }
        }
    }

    private fun sincronizarComFirebase() {
        viewModelScope.launch {
            try {
                repository.sincronizarComFirebase()
                // Escutar mudanças do Firebase
                repository.escutarReceitas { _ ->
                    // As mudanças são automaticamente refletidas no Room
                }
            } catch (e: Exception) {
                println("Erro na sincronização: ${e.message}")
            }
        }
    }

    fun adicionarReceita(
        context: Context,
        nome: String,
        descricaoCurta: String,
        imagemUri: Uri?,
        ingredientes: List<String>,
        modoPreparo: List<String>,
        tempoPreparo: String,
        porcoes: Int,
        userId: String,
        userEmail: String?
    ) {
        viewModelScope.launch {
            _uiState.value = ReceitasUiState.Loading
            try {
                val id = System.currentTimeMillis().toString()
                var imageUrl: String? = null
                if (imagemUri != null) {
                    // Usar ImageStorageService para upload de Uri
                    imageUrl = ImageStorageService().uploadImage(context, imagemUri, id)
                }
                repository.salvarReceita(
                    context = context,
                    id = id,
                    nome = nome,
                    descricaoCurta = descricaoCurta,
                    imagemUri = null, // não envia imagem para o Firebase
                    ingredientes = ingredientes,
                    modoPreparo = modoPreparo,
                    tempoPreparo = tempoPreparo,
                    porcoes = porcoes,
                    userId = userId,
                    userEmail = userEmail,
                    imagemUrl = imageUrl
                )
                _eventChannel.send("Receita adicionada com sucesso!")
            } catch (e: Exception) {
                _uiState.value = ReceitasUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun deletarReceita(id: String, imageUrl: String?) {
        viewModelScope.launch {
            _uiState.value = ReceitasUiState.Loading
            try {
                if (!imageUrl.isNullOrBlank()) {
                    ImageStorageService().deleteImage(imageUrl)
                }
                repository.deletarReceita(id, imageUrl)
                _eventChannel.send("Receita deletada com sucesso!")
            } catch (e: Exception) {
                _uiState.value = ReceitasUiState.Error(e.message ?: "Erro ao deletar receita")
            }
        }
    }

    fun curtirReceita(id: String, userId: String, curtidasAtuais: List<String>) {
        viewModelScope.launch {
            try {
                val atualizados = if (curtidasAtuais.contains(userId)) {
                    curtidasAtuais - userId
                } else {
                    curtidasAtuais + userId
                }
                repository.atualizarCurtidas(id, atualizados)
            } catch (e: Exception) {
                _eventChannel.send("Erro ao curtir receita: ${e.message}")
            }
        }
    }

    fun favoritarReceita(id: String, userId: String, favoritosAtuais: List<String>) {
        viewModelScope.launch {
            try {
                val atualizados = if (favoritosAtuais.contains(userId)) {
                    favoritosAtuais - userId
                } else {
                    favoritosAtuais + userId
                }
                repository.atualizarFavoritos(id, atualizados)
            } catch (e: Exception) {
                _eventChannel.send("Erro ao favoritar receita: ${e.message}")
            }
        }
    }

    fun editarReceita(
        context: Context,
        id: String,
        nome: String,
        descricaoCurta: String,
        novaImagemUri: Uri?,
        ingredientes: List<String>,
        modoPreparo: List<String>,
        tempoPreparo: String,
        porcoes: Int,
        imagemUrlAntiga: String?
    ) {
        viewModelScope.launch {
            _uiState.value = ReceitasUiState.Loading
            try {
                var imageUrl: String? = imagemUrlAntiga
                if (novaImagemUri != null) {
                    if (!imagemUrlAntiga.isNullOrBlank()) {
                        ImageStorageService().deleteImage(imagemUrlAntiga)
                    }
                    imageUrl = ImageStorageService().uploadImage(context, novaImagemUri, id)
                }
                
                // Buscar receita atual do Room
                val receitaAtual = receitaDao.getReceitaById(id)
                if (receitaAtual != null) {
                    val receitaAtualizada = receitaAtual.copy(
                        nome = nome,
                        descricaoCurta = descricaoCurta,
                        imagemUrl = imageUrl ?: "",
                        ingredientes = ingredientes,
                        modoPreparo = modoPreparo,
                        tempoPreparo = tempoPreparo,
                        porcoes = porcoes,
                        isSynced = connectivityObserver.isConnected(),
                        lastModified = System.currentTimeMillis()
                    )
                    
                    // Atualizar no Room
                    receitaDao.updateReceita(receitaAtualizada)
                    
                    // Se online, atualizar no Firebase
                    if (connectivityObserver.isConnected()) {
                        try {
                            repository.salvarReceitaNoFirebase(receitaAtualizada.toMap())
                            receitaDao.markAsSynced(id)
                        } catch (e: Exception) {
                            receitaDao.updateReceita(receitaAtualizada.copy(isSynced = false))
                        }
                    }
                }
                
                _eventChannel.send("Receita editada com sucesso!")
            } catch (e: Exception) {
                _uiState.value = ReceitasUiState.Error(e.message ?: "Erro ao editar receita")
            }
        }
    }
    
    // Função para buscar informações nutricionais de uma receita
    fun buscarInformacoesNutricionais(recipeTitle: String) {
        viewModelScope.launch {
            try {
                val result = nutritionRepository.getNutritionInfo(recipeTitle)
                result.fold(
                    onSuccess = { nutrition ->
                        _nutritionState.value = nutrition
                        _eventChannel.send("Informações nutricionais carregadas!")
                    },
                    onFailure = { _ ->
                        // Prover dados nutricionais padrão quando a API falha
                        val fallbackNutrition = getFallbackNutritionData(recipeTitle)
                        _nutritionState.value = fallbackNutrition
                        _eventChannel.send("Informações nutricionais estimadas carregadas")
                    }
                )
            } catch (e: Exception) {
                // Prover dados nutricionais padrão quando há erro
                val fallbackNutrition = getFallbackNutritionData(recipeTitle)
                _nutritionState.value = fallbackNutrition
                _eventChannel.send("Informações nutricionais estimadas carregadas")
            }
        }
    }
    
    // Função para fornecer dados nutricionais padrão quando a API falha
    private fun getFallbackNutritionData(recipeTitle: String): RecipeNutrition {
        return when (recipeTitle.lowercase()) {
            "bolo de cenoura" -> RecipeNutrition(350.0, 6.0, 15.0, 45.0, 2.0, 25.0)
            "feijoada" -> RecipeNutrition(450.0, 25.0, 20.0, 35.0, 8.0, 5.0)
            "mousse de chocolate" -> RecipeNutrition(280.0, 4.0, 18.0, 25.0, 1.0, 20.0)
            "pão de queijo" -> RecipeNutrition(180.0, 8.0, 12.0, 15.0, 1.0, 2.0)
            "brigadeiro" -> RecipeNutrition(120.0, 2.0, 5.0, 18.0, 0.5, 15.0)
            "coxinha" -> RecipeNutrition(220.0, 12.0, 15.0, 18.0, 1.0, 1.0)
            "lasanha" -> RecipeNutrition(380.0, 20.0, 18.0, 35.0, 3.0, 8.0)
            "panqueca" -> RecipeNutrition(200.0, 15.0, 8.0, 25.0, 2.0, 5.0)
            "tapioca" -> RecipeNutrition(150.0, 2.0, 5.0, 25.0, 1.0, 8.0)
            "omelete" -> RecipeNutrition(180.0, 12.0, 12.0, 3.0, 0.5, 1.0)
            else -> RecipeNutrition(250.0, 10.0, 12.0, 25.0, 2.0, 8.0) // Dados padrão genéricos
        }
    }
    
    // Função para limpar informações nutricionais
    fun limparInformacoesNutricionais() {
        _nutritionState.value = null
    }
    
    // Função para carregar receitas recomendadas
    private fun carregarReceitasRecomendadas(allRecipes: List<ReceitaEntity>) {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val userPreferences = getUserPreferences(currentUser.uid)
                    val recommended = generateRecommendations(allRecipes, userPreferences)
                    _recommendedRecipes.value = recommended
                }
            } catch (e: Exception) {
                println("Erro ao carregar recomendações: ${e.message}")
            }
        }
    }
    
    // Função para obter preferências do usuário
    private suspend fun getUserPreferences(userId: String): Set<String> {
        return try {
            val database = FirebaseDatabase.getInstance()
            val snapshot = database.getReference("users").child(userId).child("preferences").get().await()
            snapshot.getValue(String::class.java)?.split(",")?.toSet() ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }
    
    // Função para gerar recomendações
    private fun generateRecommendations(
        allRecipes: List<ReceitaEntity>,
        userPreferences: Set<String>
    ): List<ReceitaEntity> {
        if (userPreferences.isEmpty()) {
            // Se não há preferências, retornar receitas populares (com mais curtidas)
            return allRecipes.sortedByDescending { it.curtidas.size }.take(5)
        }
        
        val recipeScores = allRecipes.map { recipe ->
            var score = 0
            
            // Pontuar por tags que correspondem às preferências
            recipe.tags.forEach { tag ->
                if (userPreferences.contains(tag)) {
                    score += 5
                }
            }
            
            // Pontuar por receitas favoritadas
            if (recipe.favoritos.isNotEmpty()) {
                score += 10
            }
            
            // Pontuar por receitas com muitas curtidas
            score += recipe.curtidas.size
            
            recipe to score
        }
        
        return recipeScores
            .sortedByDescending { it.second }
            .take(5)
            .map { it.first }
    }
    
    // Função para atualizar recomendações quando as preferências mudam
    fun atualizarRecomendacoes() {
        val allRecipes = (uiState.value as? ReceitasUiState.Success)?.receitas ?: emptyList()
        carregarReceitasRecomendadas(allRecipes)
    }
    
    // Função para sincronizar receitas do Firebase
    fun syncFromFirebase() {
        viewModelScope.launch {
            try {
                _uiState.value = ReceitasUiState.Loading
                
                val result = repository.syncFromFirebase()
                
                if (result.isSuccess) {
                    val syncCount = result.getOrNull() ?: 0
                    if (syncCount > 0) {
                        _eventChannel.send("$syncCount receitas sincronizadas do Firebase")
                    }
                    
                    // Recarregar receitas após sincronização
                    carregarReceitas()
                } else {
                    _eventChannel.send("Erro ao sincronizar do Firebase")
                }
            } catch (e: Exception) {
                _eventChannel.send("Erro ao sincronizar: ${e.message}")
            }
        }
    }
}
