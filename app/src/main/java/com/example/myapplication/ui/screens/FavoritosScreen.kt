package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.data.FirebaseRepository
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.screens.ReceitasViewModel
import com.example.myapplication.ui.screens.ReceitasUiState
import com.example.myapplication.ui.screens.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(navController: NavHostController) {
    val navBackStackEntry = navController.getBackStackEntry(AppScreens.TelaInicialScreen.route)
    val receitasViewModel: ReceitasViewModel = viewModel(viewModelStoreOwner = navBackStackEntry)
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
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
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
                    val favList = it["favoritos"] as? List<String> ?: emptyList()
                    currentUser != null && favList.contains(currentUser.uid)
                }
                if (favoritos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text("Nenhuma receita favorita adicionada ainda.")
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
                                receita = receita,
                                onClick = {
                                    val id = receita["id"]?.toString()
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(id))
                                },
                                onEdit = {
                                    val id = receita["id"]?.toString()
                                    navController.navigate(AppScreens.DetalheScreen.createRoute(id, startInEditMode = true))
                                },
                                onDelete = {
                                    val id = receita["id"]?.toString() ?: return@ReceitaCardFirebase
                                    val imagemUrl = receita["imagemUrl"] as? String
                                    receitasViewModel.deletarReceita(id, imagemUrl)
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
                val msg = (state as ReceitasUiState.Error).message
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(msg)
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
