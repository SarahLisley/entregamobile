package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myapplication.ui.components.CachedImage
import com.example.myapplication.ui.components.ReceitasPaginatedListWithSearch
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.SwipeableRecipeCard
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.feature.receitas.ReceitasViewModel
import com.example.myapplication.ui.screens.ViewModelFactory

import com.example.myapplication.feature.receitas.ReceitasUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaScreen(navController: NavHostController) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    val navBackStackEntry = remember(navController.currentBackStackEntry?.destination?.route) {
        navController.getBackStackEntry(AppScreens.TelaInicialScreen.route)
    }
    val receitasViewModel: ReceitasViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry,
        factory = ViewModelFactory(context)
    )
    val uiState by receitasViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    // Coleta eventos únicos do ViewModel para exibir Snackbars
    LaunchedEffect(Unit) {
        receitasViewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    val filteredReceitas = remember(searchText, uiState) {
        if (uiState is ReceitasUiState.Success) {
            val receitas = (uiState as ReceitasUiState.Success).receitas
            if (searchText.isBlank()) {
                receitas
            } else {
                receitas.filter {
                    it.nome.contains(searchText, ignoreCase = true) ||
                            it.ingredientes.any { ingrediente ->
                                ingrediente.contains(searchText, ignoreCase = true)
                            }
                }
            }
        } else {
            emptyList()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Busca") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar Receitas") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState is ReceitasUiState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState is ReceitasUiState.Error) {
                val msg = (uiState as ReceitasUiState.Error).error.message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(msg)
                }
            } else {
                if (filteredReceitas.isEmpty()) {
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
                            Icon(
                                imageVector = if (searchText.isBlank()) Icons.Filled.Search else Icons.Filled.SearchOff,
                                contentDescription = "Busca vazia",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchText.isBlank()) "Nenhuma receita encontrada" else "Nenhum resultado para \"$searchText\"",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (searchText.isBlank()) 
                                    "Digite o nome de uma receita ou ingrediente para começar a buscar" 
                                else 
                                    "Tente usar termos diferentes ou verificar a ortografia",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    LazyColumn {
                        items(filteredReceitas) { receita ->
                            SwipeableRecipeCard(
                                receita = receita,
                                onSwipeToFavorite = {
                                    receitasViewModel.favoritarReceita(receita.id, receita.userId, receita.favoritos)
                                },
                                onCardClick = {
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
                                },
                                onFavoriteClick = { isFavorite ->
                                    receitasViewModel.favoritarReceita(receita.id, receita.userId, receita.favoritos)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
