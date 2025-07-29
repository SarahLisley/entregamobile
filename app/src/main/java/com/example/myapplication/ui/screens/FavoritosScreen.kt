package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.SwipeableRecipeCard
import com.example.myapplication.core.data.repository.ReceitasRepository
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.feature.receitas.ReceitasViewModel
import com.example.myapplication.ui.screens.ViewModelFactory
import com.example.myapplication.feature.receitas.ReceitasUiState
import com.example.myapplication.ui.screens.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(navController: NavHostController) {
    val context = LocalContext.current
    val navBackStackEntry = navController.getBackStackEntry(AppScreens.TelaInicialScreen.route)
    val receitasViewModel: ReceitasViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry,
        factory = ViewModelFactory(context)
    )
    val authViewModel: AuthViewModel = viewModel()
    val uiState by receitasViewModel.uiState.collectAsState()
    val currentUser = authViewModel.usuarioAtual()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Estados para funcionalidades avançadas
    var showGridView by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("todos") }
    var selectedSort by remember { mutableStateOf("recentes") }
    var showPersonalNotes by remember { mutableStateOf(false) }
    var selectedRecipeForNotes by remember { mutableStateOf<com.example.myapplication.core.data.database.entity.ReceitaEntity?>(null) }
    
    // Estatísticas
    var totalFavorites by remember { mutableStateOf(0) }
    var weeklyFavorites by remember { mutableStateOf(0) }
    var mostPopularFavorite by remember { mutableStateOf<com.example.myapplication.core.data.database.entity.ReceitaEntity?>(null) }
    
    // Filtros disponíveis
    val availableFilters = listOf(
        "todos" to "Todos",
        "doces" to "Doces",
        "salgados" to "Salgados",
        "rapidos" to "Rápidos",
        "saudaveis" to "Saudáveis"
    )
    
    // Ordenações disponíveis
    val availableSorts = listOf(
        "recentes" to "Mais Recentes",
        "antigas" to "Mais Antigas",
        "populares" to "Mais Populares",
        "tempo" to "Por Tempo",
        "porcoes" to "Por Porções"
    )
    
    // Coleta eventos únicos do ViewModel para exibir Snackbars
    LaunchedEffect(Unit) {
        receitasViewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    
    // Carregar estatísticas
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            loadFavoritesStatistics(userId) { stats ->
                totalFavorites = (stats["total"] as? Int) ?: 0
                weeklyFavorites = (stats["weekly"] as? Int) ?: 0
                mostPopularFavorite = stats["mostPopular"] as? com.example.myapplication.core.data.database.entity.ReceitaEntity
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "Meus Favoritos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (totalFavorites > 0) {
                            Text(
                                text = "$totalFavorites receitas favoritas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showGridView = !showGridView }) {
                        Icon(
                            imageVector = if (showGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = if (showGridView) "Lista" else "Grid"
                        )
                    }
                    IconButton(onClick = { showPersonalNotes = true }) {
                        Icon(Icons.AutoMirrored.Filled.Note, contentDescription = "Notas Pessoais")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when(val state = uiState) {
            is ReceitasUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ReceitasUiState.Success -> {
                val favoritos = state.receitas.filter {
                    currentUser != null && it.favoritos.contains(currentUser.uid)
                }
                
                if (favoritos.isEmpty()) {
                    EmptyFavoritesState()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Estatísticas e filtros
                        FavoritesStatsAndFilters(
                            totalFavorites = totalFavorites,
                            weeklyFavorites = weeklyFavorites,
                            mostPopularFavorite = mostPopularFavorite,
                            selectedFilter = selectedFilter,
                            selectedSort = selectedSort,
                            availableFilters = availableFilters,
                            availableSorts = availableSorts,
                            onFilterChange = { selectedFilter = it },
                            onSortChange = { selectedSort = it }
                        )
                        
                        // Lista de favoritos
                        val filteredAndSortedFavorites = getFilteredAndSortedFavorites(
                            favoritos, selectedFilter, selectedSort
                        )
                        
                        if (showGridView) {
                            FavoritesGridView(
                                favoritos = filteredAndSortedFavorites,
                                onRecipeClick = { receita ->
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
                                },
                                onFavoriteClick = { id, userId, favoritos ->
                                    receitasViewModel.favoritarReceita(id, userId, favoritos)
                                },
                                onShareClick = { receita ->
                                    // Implementar compartilhamento
                                }
                            )
                        } else {
                            FavoritesListView(
                                favoritos = filteredAndSortedFavorites,
                                onRecipeClick = { receita ->
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
                                },
                                onFavoriteClick = { id, userId, favoritos ->
                                    receitasViewModel.favoritarReceita(id, userId, favoritos)
                                },
                                onShareClick = { receita ->
                                    // Implementar compartilhamento
                                }
                            )
                        }
                    }
                }
            }
            is ReceitasUiState.Error -> {
                val msg = state.error.message
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(msg)
                }
            }
            ReceitasUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    
    // Dialog para notas pessoais
    if (showPersonalNotes) {
        PersonalNotesDialog(
            onDismiss = { showPersonalNotes = false },
            onSave = { recipeId, note ->
                // Salvar nota pessoal
                currentUser?.uid?.let { userId ->
                    savePersonalNote(userId, recipeId, note)
                }
                showPersonalNotes = false
            }
        )
    }
}

@Composable
fun EmptyFavoritesState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.size(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Favoritos vazios",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Nenhuma receita favorita ainda",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Explore receitas e adicione suas favoritas para encontrá-las aqui facilmente!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Navegar para explorar receitas */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Explorar Receitas")
            }
        }
    }
}

@Composable
fun FavoritesStatsAndFilters(
    totalFavorites: Int,
    weeklyFavorites: Int,
    mostPopularFavorite: com.example.myapplication.core.data.database.entity.ReceitaEntity?,
    selectedFilter: String,
    selectedSort: String,
    availableFilters: List<Pair<String, String>>,
    availableSorts: List<Pair<String, String>>,
    onFilterChange: (String) -> Unit,
    onSortChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Estatísticas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Suas Estatísticas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FavoriteStatItem(
                        icon = Icons.Default.Favorite,
                        value = totalFavorites.toString(),
                        label = "Total"
                    )
                    FavoriteStatItem(
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        value = weeklyFavorites.toString(),
                        label = "Esta Semana"
                    )
                    FavoriteStatItem(
                        icon = Icons.Default.Star,
                        value = if (mostPopularFavorite != null) "⭐" else "0",
                        label = "Mais Popular"
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Filtros e ordenação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Filtros
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableFilters) { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { onFilterChange(key) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Ordenação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ordenar por:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableSorts) { (key, label) ->
                    FilterChip(
                        selected = selectedSort == key,
                        onClick = { onSortChange(key) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteStatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun FavoritesGridView(
    favoritos: List<com.example.myapplication.core.data.database.entity.ReceitaEntity>,
    onRecipeClick: (com.example.myapplication.core.data.database.entity.ReceitaEntity) -> Unit,
    onFavoriteClick: (String, String, List<String>) -> Unit,
    onShareClick: (com.example.myapplication.core.data.database.entity.ReceitaEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(favoritos.chunked(2)) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { receita ->
                    EnhancedFavoriteCard(
                        receita = receita,
                        modifier = Modifier.weight(1f),
                        onClick = { onRecipeClick(receita) },
                        onFavoriteClick = { onFavoriteClick(receita.id, receita.userId, receita.favoritos) },
                        onShareClick = { onShareClick(receita) }
                    )
                }
                
                // Preencher espaço vazio se necessário
                repeat(2 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun FavoritesListView(
    favoritos: List<com.example.myapplication.core.data.database.entity.ReceitaEntity>,
    onRecipeClick: (com.example.myapplication.core.data.database.entity.ReceitaEntity) -> Unit,
    onFavoriteClick: (String, String, List<String>) -> Unit,
    onShareClick: (com.example.myapplication.core.data.database.entity.ReceitaEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(favoritos) { receita ->
            SwipeableRecipeCard(
                receita = receita,
                onSwipeToFavorite = {
                    // Como já é favorito, não faz nada
                },
                onCardClick = { onRecipeClick(receita) },
                onFavoriteClick = { isFavorite ->
                    onFavoriteClick(receita.id, receita.userId, receita.favoritos)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EnhancedFavoriteCard(
    receita: com.example.myapplication.core.data.database.entity.ReceitaEntity,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Imagem com overlay
            Box {
                AsyncImage(
                    model = receita.imagemUrl,
                    contentDescription = receita.nome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                
                // Badge de favorito
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "❤️ Favorito",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                // Botões de ação
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Desfavoritar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartilhar",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = receita.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = receita.descricaoCurta,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Informações rápidas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = receita.tempoPreparo,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${receita.porcoes} porções",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${receita.curtidas?.size ?: 0}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalNotesDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    var recipeId by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Adicionar Nota Pessoal")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Sua nota pessoal") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(recipeId, note) }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Funções auxiliares
private fun getFilteredAndSortedFavorites(
    favoritos: List<com.example.myapplication.core.data.database.entity.ReceitaEntity>,
    filter: String,
    sort: String
): List<com.example.myapplication.core.data.database.entity.ReceitaEntity> {
    var filtered = favoritos
    
    // Aplicar filtros
    when (filter) {
        "doces" -> filtered = favoritos.filter { isSweetRecipe(it) }
        "salgados" -> filtered = favoritos.filter { isSavoryRecipe(it) }
        "rapidos" -> filtered = favoritos.filter { isQuickRecipe(it) }
        "saudaveis" -> filtered = favoritos.filter { isHealthyRecipe(it) }
    }
    
    // Aplicar ordenação
    return when (sort) {
        "recentes" -> filtered.sortedByDescending { it.lastModified }
        "antigas" -> filtered.sortedBy { it.lastModified }
        "populares" -> filtered.sortedByDescending { it.curtidas?.size ?: 0 }
        "tempo" -> filtered.sortedBy { extractTimeInMinutes(it.tempoPreparo) }
        "porcoes" -> filtered.sortedBy { it.porcoes }
        else -> filtered
    }
}

private fun isSweetRecipe(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity): Boolean {
    val sweetIngredients = setOf("açúcar", "mel", "chocolate", "doce", "sobremesa", "bolo", "torta")
    return receita.ingredientes.any { ingrediente ->
        sweetIngredients.any { sweet -> ingrediente.contains(sweet, ignoreCase = true) }
    }
}

private fun isSavoryRecipe(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity): Boolean {
    val savoryIngredients = setOf("carne", "frango", "peixe", "sal", "queijo", "legume")
    return receita.ingredientes.any { ingrediente ->
        savoryIngredients.any { savory -> ingrediente.contains(savory, ignoreCase = true) }
    }
}

private fun isQuickRecipe(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity): Boolean {
    val prepTime = receita.tempoPreparo.lowercase()
    return prepTime.contains("15") || prepTime.contains("20") || prepTime.contains("rápido")
}

private fun isHealthyRecipe(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity): Boolean {
    val healthyIngredients = setOf("legume", "fruta", "verdura", "grão", "integral")
    val healthyCount = receita.ingredientes.count { ingrediente ->
        healthyIngredients.any { healthy -> ingrediente.contains(healthy, ignoreCase = true) }
    }
    return healthyCount >= 2
}

private fun extractTimeInMinutes(timeString: String): Int {
    return try {
        timeString.replace(Regex("[^0-9]"), "").toInt()
    } catch (e: Exception) {
        0
    }
}

private suspend fun loadFavoritesStatistics(userId: String, onStatsLoaded: (Map<String, Any?>) -> Unit) {
    try {
        val database = FirebaseDatabase.getInstance()
        val snapshot = database.getReference("receitas").get().await()
        
        var totalFavorites = 0
        var weeklyFavorites = 0
        var mostPopularFavorite: com.example.myapplication.core.data.database.entity.ReceitaEntity? = null
        var maxFavorites = 0
        
        snapshot.children.forEach { recipeSnapshot ->
            val favoritos = recipeSnapshot.child("favoritos").getValue(List::class.java) as? List<String>
            if (favoritos?.contains(userId) == true) {
                totalFavorites++
                
                // Verificar se foi favoritado esta semana
                val lastModified = recipeSnapshot.child("lastModified").getValue(Long::class.java) ?: 0
                val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
                if (lastModified > weekAgo) {
                    weeklyFavorites++
                }
                
                // Verificar se é o mais popular
                val favoritesCount = favoritos.size
                if (favoritesCount > maxFavorites) {
                    maxFavorites = favoritesCount
                    // Aqui você precisaria converter o snapshot para ReceitaEntity
                    // Por simplicidade, vamos deixar como null por enquanto
                }
            }
        }
        
        onStatsLoaded(
            mapOf(
                "total" to totalFavorites,
                "weekly" to weeklyFavorites,
                "mostPopular" to mostPopularFavorite
            )
        )
    } catch (e: Exception) {
        println("Erro ao carregar estatísticas: ${e.message}")
        onStatsLoaded(emptyMap())
    }
}

private fun savePersonalNote(userId: String, recipeId: String, note: String) {
    val database = FirebaseDatabase.getInstance()
    database.getReference("users").child(userId).child("personalNotes").child(recipeId).setValue(note)
}
