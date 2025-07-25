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
import coil.compose.AsyncImage
import com.example.myapplication.data.FirebaseRepository
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.screens.ReceitasViewModel
import com.example.myapplication.ui.screens.ReceitasUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    val navBackStackEntry = navController.getBackStackEntry(AppScreens.TelaInicialScreen.route)
    val receitasViewModel: ReceitasViewModel = viewModel(viewModelStoreOwner = navBackStackEntry)
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
                    val nome = it["nome"] as? String ?: ""
                    val ingredientes = it["ingredientes"] as? List<*> ?: emptyList<String>()
                    nome.contains(searchText, ignoreCase = true) ||
                            ingredientes.any { ingrediente ->
                                ingrediente.toString().contains(searchText, ignoreCase = true)
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
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
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
                val msg = (uiState as ReceitasUiState.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(msg)
                }
            } else {
                LazyColumn {
                    items(filteredReceitas) { receita ->
                        ReceitaCardFirebase(
                            receita = receita,
                            onClick = {
                                val id = receita["id"]?.toString() ?: ""
                                navController.navigate(AppScreens.DetalheScreen.createRoute(id))
                            },
                            onEdit = {},
                            onDelete = {}
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
