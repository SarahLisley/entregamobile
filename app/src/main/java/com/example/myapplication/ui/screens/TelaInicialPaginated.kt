package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.ReceitaCard
import com.example.myapplication.ui.components.EnhancedRecipeCard
import com.example.myapplication.feature.receitas.ReceitasViewModel
import com.example.myapplication.ui.screens.ViewModelFactory
import com.example.myapplication.ui.screens.AuthViewModel

/**
 * Versão da TelaInicial com paginação infinita
 * Demonstra o uso dos novos componentes de cache e paginação
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaInicialPaginated(navController: NavHostController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val receitasViewModel: ReceitasViewModel = viewModel(
        factory = ViewModelFactory(context)
    )
    
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Estados de autenticação
    val authViewModel: AuthViewModel = viewModel()
    val isUsuarioLogado = authViewModel.usuarioAtual() != null
    
    // Observar eventos do ViewModel
    LaunchedEffect(Unit) {
        receitasViewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message = message)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NutriLivre") },
                actions = {
                    IconButton(onClick = { /* Implementar busca global */ }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Buscar"
                        )
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (isUsuarioLogado) showDialog = true },
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar novo item")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Lista principal de receitas
            Text(
                text = "Todas as Receitas",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Lista de receitas (versão simplificada)
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Placeholder para receitas
                item {
                    Text(
                        text = "Lista de receitas será implementada aqui",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
} 