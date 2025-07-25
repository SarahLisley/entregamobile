package com.example.myapplication.ui.screens

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FirebaseRepository
import com.example.myapplication.data.SupabaseImageUploader
import com.example.myapplication.data.NutritionRepository
import com.example.myapplication.model.RecipeNutrition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

sealed class ReceitasUiState {
    object Loading : ReceitasUiState()
    data class Success(val receitas: List<Map<String, Any>>) : ReceitasUiState()
    data class Error(val message: String) : ReceitasUiState()
}

class ReceitasViewModel(
    private val app: Application
) : AndroidViewModel(app) {
    private val repository = FirebaseRepository()
    private val nutritionRepository = NutritionRepository()
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
    }

    fun carregarReceitas() {
        _uiState.value = ReceitasUiState.Loading
        repository.escutarReceitas { data ->
            val receitas = data?.values?.mapNotNull { it as? Map<String, Any> } ?: emptyList()
            _uiState.value = ReceitasUiState.Success(receitas)
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
                var imageUrl: String? = null
                if (imagemUri != null) {
                    imageUrl = SupabaseImageUploader.uploadImage(context, imagemUri)
                }
                val id = System.currentTimeMillis().toString()
                repository.salvarReceita(
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
                carregarReceitas()
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
                    SupabaseImageUploader.deleteImageByUrl(imageUrl)
                }
                repository.db.child(id).removeValue()
                _eventChannel.send("Receita deletada com sucesso!")
                carregarReceitas()
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
                repository.db.child(id).child("curtidas").setValue(atualizados)
            } catch (_: Exception) {}
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
                repository.db.child(id).child("favoritos").setValue(atualizados)
            } catch (_: Exception) {}
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
                        SupabaseImageUploader.deleteImageByUrl(imagemUrlAntiga)
                    }
                    imageUrl = SupabaseImageUploader.uploadImage(context, novaImagemUri)
                }
                val updateMap = mapOf(
                    "nome" to nome,
                    "descricaoCurta" to descricaoCurta,
                    "tempoPreparo" to tempoPreparo,
                    "porcoes" to porcoes,
                    "ingredientes" to ingredientes,
                    "modoPreparo" to modoPreparo,
                    "imagemUrl" to (imageUrl ?: "")
                )
                repository.db.child(id).updateChildren(updateMap)
                _eventChannel.send("Receita editada com sucesso!")
                carregarReceitas()
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
                    onFailure = { exception ->
                        _nutritionState.value = null
                        _eventChannel.send("Erro ao carregar informações nutricionais: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _nutritionState.value = null
                _eventChannel.send("Erro ao buscar informações nutricionais: ${e.message}")
            }
        }
    }
    
    // Função para limpar informações nutricionais
    fun limparInformacoesNutricionais() {
        _nutritionState.value = null
    }
}
