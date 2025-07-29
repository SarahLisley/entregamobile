package com.example.myapplication.feature.receitas

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.ui.error.ErrorHandler
import com.example.myapplication.core.ui.error.UserFriendlyError
import com.example.myapplication.core.ui.error.ErrorType
import com.example.myapplication.core.data.repository.AuthRepository
import com.example.myapplication.core.data.repository.NutritionRepository
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.repository.IReceitasRepository
// import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
// import javax.inject.Inject
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await


sealed class ReceitasUiState {
    object Loading : ReceitasUiState()
    data class Success(val receitas: List<ReceitaEntity>) : ReceitasUiState()
    data class Error(val error: UserFriendlyError) : ReceitasUiState()
}

class ReceitasViewModel(
    private val receitasRepository: IReceitasRepository,
    private val nutritionRepository: NutritionRepository,
    private val errorHandler: ErrorHandler,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ReceitasUiState>(ReceitasUiState.Loading)
    val uiState: StateFlow<ReceitasUiState> = _uiState

    // Estado para a receita selecionada (independente da lista)
    private val _selectedRecipeState = MutableStateFlow<ReceitasUiState>(ReceitasUiState.Loading)
    val selectedRecipeState: StateFlow<ReceitasUiState> = _selectedRecipeState

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
                    // Carregar recomendações após as receitas serem carregadas
                    carregarReceitasRecomendadas(receitas)
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
        porcoes: Int
    ) {
        val currentUserId = authRepository.currentUserId
        val currentUserEmail = authRepository.currentUserEmail
        
        if (currentUserId == null) {
            // Usuário não está logado
            return
        }
        
        viewModelScope.launch {
            _uiState.value = ReceitasUiState.Loading
            try {
                val id = "recipe_${System.currentTimeMillis()}"
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
                    userId = currentUserId,
                    userEmail = currentUserEmail,
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
            try {
                // Verificar se o usuário atual é o proprietário da receita
                val receita = receitasRepository.getReceitaById(id)
                if (receita == null) {
                    _eventChannel.send("Receita não encontrada!")
                    return@launch
                }
                
                // Verificar permissão: apenas o proprietário pode deletar
                val currentUserId = authRepository.currentUserId
                if (currentUserId != null && receita.userId != currentUserId) {
                    _eventChannel.send("Você não tem permissão para deletar esta receita!")
                    return@launch
                }
                
                receitasRepository.deletarReceita(id, imageUrl)
                    .onSuccess {
                        _eventChannel.send("Receita deletada com sucesso!")
                    }
                    .onFailure { exception ->
                        val userError = errorHandler.handleError(exception)
                        _uiState.value = ReceitasUiState.Error(userError)
                    }
            } catch (e: Exception) {
                val userError = errorHandler.handleError(e)
                _uiState.value = ReceitasUiState.Error(userError)
            }
        }
    }

    fun curtirReceita(id: String, userId: String, curtidasAtuais: List<String>) {
        viewModelScope.launch {
            try {
                android.util.Log.d("ReceitasViewModel", "Iniciando curtir receita: $id, userId: $userId")
                val atualizados = if (curtidasAtuais.contains(userId)) {
                    android.util.Log.d("ReceitasViewModel", "Removendo curtida do usuário")
                    curtidasAtuais - userId
                } else {
                    android.util.Log.d("ReceitasViewModel", "Adicionando curtida do usuário")
                    curtidasAtuais + userId
                }
                
                android.util.Log.d("ReceitasViewModel", "Curtidas atualizadas: $atualizados")
                
                receitasRepository.atualizarCurtidas(id, atualizados)
                    .onSuccess {
                        android.util.Log.d("ReceitasViewModel", "Curtida atualizada com sucesso")
                        _eventChannel.send("Curtida atualizada!")
                    }
                    .onFailure { exception ->
                        android.util.Log.e("ReceitasViewModel", "Erro ao curtir receita: ${exception.message}")
                        val userError = errorHandler.handleError(exception)
                        _eventChannel.send("Erro ao curtir receita: ${userError.message}")
                    }
            } catch (e: Exception) {
                android.util.Log.e("ReceitasViewModel", "Exceção ao curtir receita: ${e.message}")
                val userError = errorHandler.handleError(e)
                _eventChannel.send("Erro ao curtir receita: ${userError.message}")
            }
        }
    }

    fun favoritarReceita(id: String, userId: String, favoritosAtuais: List<String>) {
        viewModelScope.launch {
            try {
                android.util.Log.d("ReceitasViewModel", "Iniciando favoritar receita: $id, userId: $userId")
                val atualizados = if (favoritosAtuais.contains(userId)) {
                    android.util.Log.d("ReceitasViewModel", "Removendo favorito do usuário")
                    favoritosAtuais - userId
                } else {
                    android.util.Log.d("ReceitasViewModel", "Adicionando favorito do usuário")
                    favoritosAtuais + userId
                }
                
                android.util.Log.d("ReceitasViewModel", "Favoritos atualizados: $atualizados")
                
                receitasRepository.atualizarFavoritos(id, atualizados)
                    .onSuccess {
                        android.util.Log.d("ReceitasViewModel", "Favorito atualizado com sucesso")
                        _eventChannel.send("Favorito atualizado!")
                    }
                    .onFailure { exception ->
                        android.util.Log.e("ReceitasViewModel", "Erro ao favoritar receita: ${exception.message}")
                        val userError = errorHandler.handleError(exception)
                        _eventChannel.send("Erro ao favoritar receita: ${userError.message}")
                    }
            } catch (e: Exception) {
                android.util.Log.e("ReceitasViewModel", "Exceção ao favoritar receita: ${e.message}")
                val userError = errorHandler.handleError(e)
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
                android.util.Log.d("ReceitasViewModel", "Iniciando edição da receita: $id")
                
                // Buscar receita atual do Room
                val receitaAtual = receitasRepository.getReceitaById(id)
                if (receitaAtual == null) {
                    android.util.Log.e("ReceitasViewModel", "Receita não encontrada: $id")
                    _eventChannel.send("Receita não encontrada!")
                    return@launch
                }
                
                // Verificar permissão: apenas o proprietário pode editar
                val currentUserId = authRepository.currentUserId
                if (currentUserId != null && receitaAtual.userId != currentUserId) {
                    android.util.Log.w("ReceitasViewModel", "Usuário não tem permissão para editar: $currentUserId vs ${receitaAtual.userId}")
                    _eventChannel.send("Você não tem permissão para editar esta receita!")
                    return@launch
                }
                
                android.util.Log.d("ReceitasViewModel", "Permissão verificada, prosseguindo com edição")
                
                var imageUrl = imagemUrlAntiga
                
                // Se há uma nova imagem, fazer upload
                if (novaImagemUri != null) {
                    android.util.Log.d("ReceitasViewModel", "Fazendo upload da nova imagem")
                    imageUrl = receitasRepository.updateImage(
                        context, novaImagemUri, imagemUrlAntiga, id
                    )
                    android.util.Log.d("ReceitasViewModel", "Nova URL da imagem: $imageUrl")
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
                
                android.util.Log.d("ReceitasViewModel", "Receita atualizada criada, salvando no repositório")
                
                // Atualizar no repositório
                receitasRepository.updateReceita(receitaAtualizada)
                    .onSuccess {
                        android.util.Log.d("ReceitasViewModel", "Receita editada com sucesso")
                        _eventChannel.send("Receita editada com sucesso!")
                    }
                    .onFailure { exception ->
                        android.util.Log.e("ReceitasViewModel", "Erro ao editar receita: ${exception.message}")
                        val userError = errorHandler.handleError(exception)
                        _uiState.value = ReceitasUiState.Error(userError)
                    }
            } catch (e: Exception) {
                android.util.Log.e("ReceitasViewModel", "Exceção ao editar receita: ${e.message}")
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
    
    // Função para sincronizar receitas do Firebase
    fun syncFromFirebase() {
        viewModelScope.launch {
            try {
                _uiState.value = ReceitasUiState.Loading
                
                val result = receitasRepository.syncFromFirebase()
                
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
    
    // Estado para receitas recomendadas
    private val _recommendedRecipes = MutableStateFlow<List<ReceitaEntity>>(emptyList())
    val recommendedRecipes: StateFlow<List<ReceitaEntity>> = _recommendedRecipes
    
    // Função melhorada para carregar receitas recomendadas
    private fun carregarReceitasRecomendadas(allRecipes: List<ReceitaEntity>) {
        viewModelScope.launch {
            try {
                val currentUserId = authRepository.currentUserId
                val userPreferences = getUserPreferences(currentUserId)
                val userPersonalData = getUserPersonalData(currentUserId)
                
                val recommended = generatePersonalizedRecommendations(
                    allRecipes = allRecipes,
                    userPreferences = userPreferences,
                    userPersonalData = userPersonalData,
                    currentUserId = currentUserId
                )
                
                _recommendedRecipes.value = recommended
            } catch (e: Exception) {
                println("Erro ao carregar recomendações: ${e.message}")
                // Fallback para recomendações básicas
                _recommendedRecipes.value = allRecipes.take(5)
            }
        }
    }
    
    private suspend fun getUserPreferences(userId: String?): Set<String> {
        if (userId == null) return emptySet()
        
        return try {
            val database = FirebaseDatabase.getInstance()
            val snapshot = database.getReference("users").child(userId).child("preferences").get().await()
            snapshot.getValue(String::class.java)?.split(",")?.toSet() ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }
    
    private suspend fun getUserPersonalData(userId: String?): Map<String, String> {
        if (userId == null) return emptyMap()
        
        return try {
            val database = FirebaseDatabase.getInstance()
            val snapshot = database.getReference("users").child(userId).child("personalData").get().await()
            snapshot.getValue(Map::class.java) as? Map<String, String> ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    private fun generatePersonalizedRecommendations(
        allRecipes: List<ReceitaEntity>,
        userPreferences: Set<String>,
        userPersonalData: Map<String, String>,
        currentUserId: String?
    ): List<ReceitaEntity> {
        val scoredRecipes = allRecipes.map { recipe ->
            var score = 0.0
            
            // Score baseado em preferências alimentares
            score += calculatePreferenceScore(recipe, userPreferences)
            
            // Score baseado em dados pessoais (objetivos, restrições)
            score += calculatePersonalDataScore(recipe, userPersonalData)
            
            // Score baseado em popularidade
            score += calculatePopularityScore(recipe)
            
            // Score baseado em histórico do usuário
            score += calculateUserHistoryScore(recipe, currentUserId)
            
            // Score baseado em sazonalidade
            score += calculateSeasonalityScore(recipe)
            
            // Score baseado em complexidade vs tempo disponível
            score += calculateComplexityScore(recipe, userPersonalData)
            
            recipe to score
        }
        
        return scoredRecipes
            .sortedByDescending { it.second }
            .take(8)
            .map { it.first }
    }
    
    private fun calculatePreferenceScore(recipe: ReceitaEntity, preferences: Set<String>): Double {
        var score = 0.0
        
        // Verificar se a receita atende às preferências do usuário
        val recipeTags = extractRecipeTags(recipe)
        
        preferences.forEach { preference ->
            when (preference) {
                "vegetariano" -> if (isVegetarian(recipe)) score += 10.0
                "vegano" -> if (isVegan(recipe)) score += 10.0
                "sem-gluten" -> if (isGlutenFree(recipe)) score += 8.0
                "sem-lactose" -> if (isLactoseFree(recipe)) score += 8.0
                "low-carb" -> if (isLowCarb(recipe)) score += 7.0
                "sem-acucar" -> if (isSugarFree(recipe)) score += 7.0
                "rapido" -> if (isQuickRecipe(recipe)) score += 6.0
                "saudavel" -> if (isHealthy(recipe)) score += 6.0
                "proteico" -> if (isHighProtein(recipe)) score += 5.0
                "fibras" -> if (isHighFiber(recipe)) score += 5.0
                "sem-oleo" -> if (isOilFree(recipe)) score += 6.0
                "sem-sal" -> if (isSaltFree(recipe)) score += 6.0
                "vitaminas" -> if (isHighVitamins(recipe)) score += 5.0
            }
        }
        
        return score
    }
    
    private fun calculatePersonalDataScore(recipe: ReceitaEntity, personalData: Map<String, String>): Double {
        var score = 0.0
        
        val goal = personalData["goal"] ?: ""
        val weight = personalData["weight"]?.toDoubleOrNull() ?: 0.0
        val height = personalData["height"]?.toDoubleOrNull() ?: 0.0
        val activityLevel = personalData["activityLevel"] ?: ""
        val allergies = personalData["allergies"] ?: ""
        
        // Score baseado no objetivo
        when (goal) {
            "emagrecer" -> {
                if (isLowCalorie(recipe)) score += 8.0
                if (isHighFiber(recipe)) score += 5.0
                if (isLowFat(recipe)) score += 6.0
            }
            "ganhar-massa" -> {
                if (isHighProtein(recipe)) score += 10.0
                if (isHighCalorie(recipe)) score += 6.0
            }
            "controlar-diabetes" -> {
                if (isLowGlycemic(recipe)) score += 10.0
                if (isSugarFree(recipe)) score += 8.0
                if (isHighFiber(recipe)) score += 6.0
            }
            "reducao-colesterol" -> {
                if (isLowFat(recipe)) score += 8.0
                if (isHighFiber(recipe)) score += 6.0
                if (isLowCholesterol(recipe)) score += 7.0
            }
            "manter-peso" -> {
                if (isBalanced(recipe)) score += 5.0
                if (isHealthy(recipe)) score += 6.0
            }
            "melhorar-saude" -> {
                if (isHealthy(recipe)) score += 8.0
                if (isHighVitamins(recipe)) score += 6.0
                if (isHighFiber(recipe)) score += 5.0
            }
        }
        
        // Score baseado no IMC (se disponível)
        if (weight > 0 && height > 0) {
            val bmi = weight / ((height / 100) * (height / 100))
            when {
                bmi < 18.5 -> { // Abaixo do peso
                    if (isHighCalorie(recipe)) score += 5.0
                }
                bmi > 25 -> { // Acima do peso
                    if (isLowCalorie(recipe)) score += 5.0
                }
            }
        }
        
        // Score baseado no nível de atividade
        when (activityLevel) {
            "sedentario" -> {
                if (isQuickRecipe(recipe)) score += 4.0
                if (isLowCalorie(recipe)) score += 3.0
            }
            "ativo", "atleta" -> {
                if (isHighProtein(recipe)) score += 4.0
                if (isHighCalorie(recipe)) score += 3.0
            }
        }
        
        // Verificar alergias
        if (allergies.isNotBlank()) {
            val allergyList = allergies.lowercase().split(",").map { it.trim() }
            allergyList.forEach { allergy ->
                if (recipe.ingredientes.any { ingrediente ->
                    ingrediente.lowercase().contains(allergy)
                }) {
                    score -= 20.0 // Penalizar receitas com ingredientes alergênicos
                }
            }
        }
        
        return score
    }
    
    private fun calculatePopularityScore(recipe: ReceitaEntity): Double {
        var score = 0.0
        
        // Score baseado no número de curtidas
        score += (recipe.curtidas?.size ?: 0) * 0.5
        
        // Score baseado no número de favoritos
        score += (recipe.favoritos?.size ?: 0) * 0.8
        
        // Score baseado na data de modificação (receitas mais recentes)
        val daysSinceModification = (System.currentTimeMillis() - recipe.lastModified) / (1000 * 60 * 60 * 24)
        if (daysSinceModification < 7) score += 3.0 // Receita nova
        else if (daysSinceModification < 30) score += 1.0 // Receita recente
        
        return score
    }
    
    private fun calculateUserHistoryScore(recipe: ReceitaEntity, userId: String?): Double {
        if (userId == null) return 0.0
        
        var score = 0.0
        
        // Verificar se o usuário já curtiu/favoritou receitas similares
        val userLikedRecipes = getUserLikedRecipes(userId)
        val userFavoritedRecipes = getUserFavoritedRecipes(userId)
        
        // Score baseado em similaridade com receitas curtidas
        userLikedRecipes.forEach { likedRecipe ->
            score += calculateRecipeSimilarity(recipe, likedRecipe) * 2.0
        }
        
        // Score baseado em similaridade com receitas favoritadas
        userFavoritedRecipes.forEach { favoritedRecipe ->
            score += calculateRecipeSimilarity(recipe, favoritedRecipe) * 3.0
        }
        
        return score
    }
    
    private fun calculateSeasonalityScore(recipe: ReceitaEntity): Double {
        val currentMonth = java.time.LocalDate.now().monthValue
        
        // Receitas de verão (dezembro a março no Brasil)
        val summerMonths = setOf(12, 1, 2, 3)
        if (currentMonth in summerMonths) {
            if (isSummerRecipe(recipe)) return 5.0
        }
        
        // Receitas de inverno (junho a setembro no Brasil)
        val winterMonths = setOf(6, 7, 8, 9)
        if (currentMonth in winterMonths) {
            if (isWinterRecipe(recipe)) return 5.0
        }
        
        return 0.0
    }
    
    private fun calculateComplexityScore(recipe: ReceitaEntity, personalData: Map<String, String>): Double {
        val activityLevel = personalData["activityLevel"] ?: ""
        
        return when {
            activityLevel == "sedentario" && isQuickRecipe(recipe) -> 4.0
            activityLevel == "ativo" && isComplexRecipe(recipe) -> 3.0
            else -> 0.0
        }
    }
    
    // Funções auxiliares para análise de receitas
    private fun extractRecipeTags(recipe: ReceitaEntity): Set<String> {
        val tags = mutableSetOf<String>()
        
        // Analisar ingredientes para extrair tags
        recipe.ingredientes.forEach { ingrediente ->
            when {
                ingrediente.contains("carne", ignoreCase = true) -> tags.add("carne")
                ingrediente.contains("frango", ignoreCase = true) -> tags.add("frango")
                ingrediente.contains("peixe", ignoreCase = true) -> tags.add("peixe")
                ingrediente.contains("leite", ignoreCase = true) -> tags.add("lactose")
                ingrediente.contains("queijo", ignoreCase = true) -> tags.add("lactose")
                ingrediente.contains("trigo", ignoreCase = true) -> tags.add("gluten")
                ingrediente.contains("farinha", ignoreCase = true) -> tags.add("gluten")
                ingrediente.contains("açúcar", ignoreCase = true) -> tags.add("acucar")
                ingrediente.contains("óleo", ignoreCase = true) -> tags.add("oleo")
                ingrediente.contains("sal", ignoreCase = true) -> tags.add("sal")
                ingrediente.contains("ovo", ignoreCase = true) -> tags.add("ovo")
                ingrediente.contains("legume", ignoreCase = true) -> tags.add("vegetal")
                ingrediente.contains("fruta", ignoreCase = true) -> tags.add("fruta")
                ingrediente.contains("aveia", ignoreCase = true) -> tags.add("fibras")
                ingrediente.contains("cenoura", ignoreCase = true) -> tags.add("vitaminas")
                ingrediente.contains("espinafre", ignoreCase = true) -> tags.add("vitaminas")
                ingrediente.contains("brócolis", ignoreCase = true) -> tags.add("vitaminas")
            }
        }
        
        return tags
    }
    
    private fun isVegetarian(recipe: ReceitaEntity): Boolean {
        val nonVegetarianIngredients = setOf("carne", "frango", "peixe", "porco", "bacon", "presunto")
        return !recipe.ingredientes.any { ingrediente ->
            nonVegetarianIngredients.any { nonVeg -> ingrediente.contains(nonVeg, ignoreCase = true) }
        }
    }
    
    private fun isVegan(recipe: ReceitaEntity): Boolean {
        val nonVeganIngredients = setOf("carne", "frango", "peixe", "porco", "leite", "queijo", "ovo", "manteiga", "creme")
        return !recipe.ingredientes.any { ingrediente ->
            nonVeganIngredients.any { nonVegan -> ingrediente.contains(nonVegan, ignoreCase = true) }
        }
    }
    
    private fun isGlutenFree(recipe: ReceitaEntity): Boolean {
        val glutenIngredients = setOf("trigo", "farinha", "pão", "macarrão", "cevada", "centeio")
        return !recipe.ingredientes.any { ingrediente ->
            glutenIngredients.any { gluten -> ingrediente.contains(gluten, ignoreCase = true) }
        }
    }
    
    private fun isLactoseFree(recipe: ReceitaEntity): Boolean {
        val lactoseIngredients = setOf("leite", "queijo", "manteiga", "creme", "iogurte")
        return !recipe.ingredientes.any { ingrediente ->
            lactoseIngredients.any { lactose -> ingrediente.contains(lactose, ignoreCase = true) }
        }
    }
    
    private fun isLowCarb(recipe: ReceitaEntity): Boolean {
        val highCarbIngredients = setOf("arroz", "batata", "macarrão", "pão", "farinha", "açúcar")
        val carbCount = recipe.ingredientes.count { ingrediente ->
            highCarbIngredients.any { carb -> ingrediente.contains(carb, ignoreCase = true) }
        }
        return carbCount <= 1
    }
    
    private fun isSugarFree(recipe: ReceitaEntity): Boolean {
        val sugarIngredients = setOf("açúcar", "mel", "chocolate", "doce", "sobremesa")
        return !recipe.ingredientes.any { ingrediente ->
            sugarIngredients.any { sugar -> ingrediente.contains(sugar, ignoreCase = true) }
        }
    }
    
    private fun isQuickRecipe(recipe: ReceitaEntity): Boolean {
        val prepTime = recipe.tempoPreparo.lowercase()
        return prepTime.contains("15") || prepTime.contains("20") || prepTime.contains("rápido")
    }
    
    private fun isHealthy(recipe: ReceitaEntity): Boolean {
        val healthyIngredients = setOf("legume", "fruta", "verdura", "grão", "integral")
        val healthyCount = recipe.ingredientes.count { ingrediente ->
            healthyIngredients.any { healthy -> ingrediente.contains(healthy, ignoreCase = true) }
        }
        return healthyCount >= 2
    }
    
    private fun isHighProtein(recipe: ReceitaEntity): Boolean {
        val proteinIngredients = setOf("carne", "frango", "peixe", "ovo", "queijo", "legume")
        val proteinCount = recipe.ingredientes.count { ingrediente ->
            proteinIngredients.any { protein -> ingrediente.contains(protein, ignoreCase = true) }
        }
        return proteinCount >= 2
    }
    
    private fun isHighFiber(recipe: ReceitaEntity): Boolean {
        val fiberIngredients = setOf("integral", "legume", "verdura", "fruta", "grão", "aveia")
        val fiberCount = recipe.ingredientes.count { ingrediente ->
            fiberIngredients.any { fiber -> ingrediente.contains(fiber, ignoreCase = true) }
        }
        return fiberCount >= 2
    }
    
    private fun isLowCalorie(recipe: ReceitaEntity): Boolean {
        val lightIngredients = setOf("verdura", "fruta", "legume", "peixe")
        val lightCount = recipe.ingredientes.count { ingrediente ->
            lightIngredients.any { light -> ingrediente.contains(light, ignoreCase = true) }
        }
        return lightCount >= 3
    }
    
    private fun isHighCalorie(recipe: ReceitaEntity): Boolean {
        val calorieIngredients = setOf("carne", "queijo", "manteiga", "óleo", "creme")
        val calorieCount = recipe.ingredientes.count { ingrediente ->
            calorieIngredients.any { calorie -> ingrediente.contains(calorie, ignoreCase = true) }
        }
        return calorieCount >= 2
    }
    
    private fun isLowFat(recipe: ReceitaEntity): Boolean {
        val fatIngredients = setOf("óleo", "manteiga", "creme", "queijo")
        return !recipe.ingredientes.any { ingrediente ->
            fatIngredients.any { fat -> ingrediente.contains(fat, ignoreCase = true) }
        }
    }
    
    private fun isLowGlycemic(recipe: ReceitaEntity): Boolean {
        val highGlycemicIngredients = setOf("açúcar", "arroz", "batata", "pão branco")
        return !recipe.ingredientes.any { ingrediente ->
            highGlycemicIngredients.any { glycemic -> ingrediente.contains(glycemic, ignoreCase = true) }
        }
    }
    
    private fun isOilFree(recipe: ReceitaEntity): Boolean {
        val oilIngredients = setOf("óleo", "azeite", "manteiga")
        return !recipe.ingredientes.any { ingrediente ->
            oilIngredients.any { oil -> ingrediente.contains(oil, ignoreCase = true) }
        }
    }
    
    private fun isSaltFree(recipe: ReceitaEntity): Boolean {
        val saltIngredients = setOf("sal", "shoyu", "molho inglês")
        return !recipe.ingredientes.any { ingrediente ->
            saltIngredients.any { salt -> ingrediente.contains(salt, ignoreCase = true) }
        }
    }
    
    private fun isHighVitamins(recipe: ReceitaEntity): Boolean {
        val vitaminIngredients = setOf("cenoura", "espinafre", "brócolis", "tomate", "laranja", "limão")
        val vitaminCount = recipe.ingredientes.count { ingrediente ->
            vitaminIngredients.any { vitamin -> ingrediente.contains(vitamin, ignoreCase = true) }
        }
        return vitaminCount >= 2
    }
    
    private fun isLowCholesterol(recipe: ReceitaEntity): Boolean {
        val cholesterolIngredients = setOf("ovo", "manteiga", "queijo", "creme")
        return !recipe.ingredientes.any { ingrediente ->
            cholesterolIngredients.any { cholesterol -> ingrediente.contains(cholesterol, ignoreCase = true) }
        }
    }
    
    private fun isBalanced(recipe: ReceitaEntity): Boolean {
        val proteinCount = recipe.ingredientes.count { ingrediente ->
            ingrediente.contains("carne", ignoreCase = true) || ingrediente.contains("frango", ignoreCase = true) || 
            ingrediente.contains("peixe", ignoreCase = true) || ingrediente.contains("ovo", ignoreCase = true)
        }
        val carbCount = recipe.ingredientes.count { ingrediente ->
            ingrediente.contains("arroz", ignoreCase = true) || ingrediente.contains("batata", ignoreCase = true) ||
            ingrediente.contains("pão", ignoreCase = true)
        }
        val vegCount = recipe.ingredientes.count { ingrediente ->
            ingrediente.contains("legume", ignoreCase = true) || ingrediente.contains("verdura", ignoreCase = true)
        }
        
        return proteinCount >= 1 && carbCount >= 1 && vegCount >= 1
    }
    
    private fun isComplexRecipe(recipe: ReceitaEntity): Boolean {
        return recipe.modoPreparo.size > 8 || recipe.ingredientes.size > 10
    }
    
    private fun isSummerRecipe(recipe: ReceitaEntity): Boolean {
        val summerIngredients = setOf("fruta", "suco", "sorvete", "salada", "peixe", "limão")
        return recipe.ingredientes.any { ingrediente ->
            summerIngredients.any { summer -> ingrediente.contains(summer, ignoreCase = true) }
        }
    }
    
    private fun isWinterRecipe(recipe: ReceitaEntity): Boolean {
        val winterIngredients = setOf("sopa", "caldo", "chocolate quente", "chá", "carne", "queijo")
        return recipe.ingredientes.any { ingrediente ->
            winterIngredients.any { winter -> ingrediente.contains(winter, ignoreCase = true) }
        }
    }
    
    private fun calculateRecipeSimilarity(recipe1: ReceitaEntity, recipe2: ReceitaEntity): Double {
        val tags1 = extractRecipeTags(recipe1)
        val tags2 = extractRecipeTags(recipe2)
        
        val intersection = tags1.intersect(tags2).size
        val union = tags1.union(tags2).size
        
        return if (union > 0) intersection.toDouble() / union else 0.0
    }
    
    private fun getUserLikedRecipes(userId: String): List<ReceitaEntity> {
        // Implementar busca de receitas curtidas pelo usuário
        return emptyList() // Placeholder - implementar quando necessário
    }
    
    private fun getUserFavoritedRecipes(userId: String): List<ReceitaEntity> {
        // Implementar busca de receitas favoritadas pelo usuário
        return emptyList() // Placeholder - implementar quando necessário
    }

    // Função para verificar se o usuário atual pode editar uma receita
    fun canEditReceita(receita: ReceitaEntity): Boolean {
        return receitasRepository.canEditReceita(receita, authRepository.currentUserId)
    }
    
    // Função para verificar se o usuário atual pode deletar uma receita
    fun canDeleteReceita(receita: ReceitaEntity): Boolean {
        return receitasRepository.canDeleteReceita(receita, authRepository.currentUserId)
    }
    
    // Função para obter o ID do usuário atual
    fun getCurrentUserId(): String? = authRepository.currentUserId
    
    // Função para obter o email do usuário atual
    fun getCurrentUserEmail(): String? = authRepository.currentUserEmail
    
    // Função para carregar uma receita específica por ID
    fun loadRecipeById(id: String?) {
        if (id == null) {
            _selectedRecipeState.value = ReceitasUiState.Error(UserFriendlyError("Erro", "ID da receita inválido.", ErrorType.UNKNOWN))
            return
        }

        viewModelScope.launch {
            _selectedRecipeState.value = ReceitasUiState.Loading
            try {
                val receita = receitasRepository.getReceitaById(id)
                if (receita != null) {
                    _selectedRecipeState.value = ReceitasUiState.Success(listOf(receita))
                } else {
                    _selectedRecipeState.value = ReceitasUiState.Error(UserFriendlyError("Não Encontrado", "A receita não foi encontrada.", ErrorType.NOT_FOUND))
                }
            } catch (e: Exception) {
                val userError = errorHandler.handleError(e)
                _selectedRecipeState.value = ReceitasUiState.Error(userError)
            }
        }
    }
} 