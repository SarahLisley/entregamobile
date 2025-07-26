package com.example.myapplication.feature.receitas

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.ui.error.ErrorHandler
import com.example.myapplication.core.ui.error.UserFriendlyError
import com.example.myapplication.core.data.repository.NutritionRepository
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReceitasUiState {
    object Loading : ReceitasUiState()
    data class Success(val receitas: List<ReceitaEntity>) : ReceitasUiState()
    data class Error(val error: UserFriendlyError) : ReceitasUiState()
}

@HiltViewModel
class ReceitasViewModel @Inject constructor(
    private val receitasRepository: ReceitasRepository,
    private val nutritionRepository: NutritionRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ReceitasUiState>(ReceitasUiState.Loading)
    val uiState: StateFlow<ReceitasUiState> = _uiState

    // Channel para eventos únicos (ex: Snackbars)
    private val _eventChannel = Channel<String>()
    val eventFlow = _eventChannel.receiveAsFlow()
    
    // Estado para informações nutricionais
    private val _nutritionState = MutableStateFlow<RecipeNutrition?>(null)
    val nutritionState: StateFlow<RecipeNutrition?> = _nutritionState

    init {
        carregarReceitas()
        sincronizarComFirebase()
    }

    fun carregarReceitas() {
        viewModelScope.launch {
            try {
                // Observar receitas do Room (fonte única da verdade)
                receitasRepository.getReceitas().collect { receitas ->
                    _uiState.value = ReceitasUiState.Success(receitas)
                }
            } catch (e: Exception) {
                val userError = errorHandler.handleError(e)
                _uiState.value = ReceitasUiState.Error(userError)
            }
        }
    }

    private fun sincronizarComFirebase() {
        viewModelScope.launch {
            receitasRepository.sincronizarComFirebase()
                .onSuccess {
                    // Escutar mudanças do Firebase
                    receitasRepository.escutarReceitas { _ ->
                        // As mudanças são automaticamente refletidas no Room
                    }
                }
                .onFailure { exception ->
                    println("Erro na sincronização: ${exception.message}")
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
                receitasRepository.salvarReceita(
                    context = context,
                    id = id,
                    nome = nome,
                    descricaoCurta = descricaoCurta,
                    imagemUri = imagemUri,
                    ingredientes = ingredientes,
                    modoPreparo = modoPreparo,
                    tempoPreparo = tempoPreparo,
                    porcoes = porcoes,
                    userId = userId,
                    userEmail = userEmail,
                    imagemUrl = null
                ).onSuccess {
                    _eventChannel.send("Receita adicionada com sucesso!")
                }.onFailure { exception ->
                    val userError = errorHandler.handleError(exception)
                    _uiState.value = ReceitasUiState.Error(userError)
                }
            } catch (e: Exception) {
                val userError = errorHandler.handleError(e)
                _uiState.value = ReceitasUiState.Error(userError)
            }
        }
    }

    fun deletarReceita(id: String, imageUrl: String?) {
        viewModelScope.launch {
            _uiState.value = ReceitasUiState.Loading
            receitasRepository.deletarReceita(id, imageUrl)
                .onSuccess {
                    _eventChannel.send("Receita deletada com sucesso!")
                }
                .onFailure { exception ->
                    val userError = errorHandler.handleError(exception)
                    _uiState.value = ReceitasUiState.Error(userError)
                }
        }
    }

    fun curtirReceita(id: String, userId: String, curtidasAtuais: List<String>) {
        viewModelScope.launch {
            val atualizados = if (curtidasAtuais.contains(userId)) {
                curtidasAtuais - userId
            } else {
                curtidasAtuais + userId
            }
            
            receitasRepository.atualizarCurtidas(id, atualizados)
                .onFailure { exception ->
                    val userError = errorHandler.handleError(exception)
                    _eventChannel.send("Erro ao curtir receita: ${userError.message}")
                }
        }
    }

    fun favoritarReceita(id: String, userId: String, favoritosAtuais: List<String>) {
        viewModelScope.launch {
            val atualizados = if (favoritosAtuais.contains(userId)) {
                favoritosAtuais - userId
            } else {
                favoritosAtuais + userId
            }
            
            receitasRepository.atualizarFavoritos(id, atualizados)
                .onFailure { exception ->
                    val userError = errorHandler.handleError(exception)
                    _eventChannel.send("Erro ao favoritar receita: ${userError.message}")
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
                // Buscar receita atual do Room
                val receitaAtual = receitasRepository.getReceitaById(id)
                if (receitaAtual != null) {
                    var imageUrl = imagemUrlAntiga
                    
                    // Se há uma nova imagem, fazer upload
                    if (novaImagemUri != null) {
                        imageUrl = receitasRepository.updateImage(
                            context, novaImagemUri, imagemUrlAntiga, id
                        )
                    }
                    
                    val receitaAtualizada = receitaAtual.copy(
                        nome = nome,
                        descricaoCurta = descricaoCurta,
                        imagemUrl = imageUrl ?: "",
                        ingredientes = ingredientes,
                        modoPreparo = modoPreparo,
                        tempoPreparo = tempoPreparo,
                        porcoes = porcoes,
                        lastModified = System.currentTimeMillis()
                    )
                    
                    // Atualizar no repositório
                    receitasRepository.updateReceita(receitaAtualizada)
                        .onSuccess {
                            _eventChannel.send("Receita editada com sucesso!")
                        }
                        .onFailure { exception ->
                            val userError = errorHandler.handleError(exception)
                            _uiState.value = ReceitasUiState.Error(userError)
                        }
                }
            } catch (e: Exception) {
                val userError = errorHandler.handleError(e)
                _uiState.value = ReceitasUiState.Error(userError)
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
} 