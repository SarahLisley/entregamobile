package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.core.data.repository.ReceitasRepository
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.feature.receitas.ReceitasViewModel
import com.example.myapplication.ui.screens.ViewModelFactory
import com.example.myapplication.feature.receitas.ReceitasUiState
import com.example.myapplication.ui.screens.AuthViewModel

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
    // Coleta eventos únicos do ViewModel para exibir Snackbars
    LaunchedEffect(Unit) {
        receitasViewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = "Favoritos vazios",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Nenhuma receita favorita",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Explore receitas e adicione suas favoritas para encontrá-las aqui facilmente!",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(8.dp)
                    ) {
                        items(favoritos) { receita ->
                            ReceitaCardFirebase(
                                receita = receita.toMap(),
                                onClick = {
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
                                },
                                onEdit = {
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id, startInEditMode = true))
                                },
                                onDelete = {
                                    receitasViewModel.deletarReceita(receita.id, receita.imagemUrl)
                                },
                                onCurtir = { id, userId, curtidas ->
                                    receitasViewModel.curtirReceita(id, userId, curtidas)
                                },
                                onFavoritar = { id, userId, favoritos ->
                                    receitasViewModel.favoritarReceita(id, userId, favoritos)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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
}
