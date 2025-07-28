package com.example.myapplication.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.navigation.AppScreens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.core.data.database.AppDatabase
import com.example.myapplication.core.data.network.ConnectivityObserver
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.data.storage.ImageStorageService
import com.example.myapplication.core.ui.error.ErrorHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNav() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val usuario = authViewModel.usuarioAtual()
    var startDestination by remember { mutableStateOf("login") }

    LaunchedEffect(usuario) {
        startDestination = if (usuario == null) "login" else AppScreens.TelaInicialScreen.route
        if (usuario == null) {
            navController.navigate("login") {
                popUpTo(0)
            }
        } else {
            navController.navigate(AppScreens.TelaInicialScreen.route) {
                popUpTo(0)
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate(AppScreens.TelaInicialScreen.route) {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable(AppScreens.TelaInicialScreen.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Receitas") },
                        actions = {
                            TextButton(onClick = {
                                authViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo(0)
                                }
                            }) {
                                Text("Logout")
                            }
                        }
                    )
                },
                bottomBar = { BottomNavigationBar(navController) }
            ) { paddingValues ->
                TelaInicial(navController)
            }
        }
        composable(AppScreens.FavoritosScreen.route) {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Favoritos") })
                },
                bottomBar = { BottomNavigationBar(navController) }
            ) { paddingValues ->
                FavoritosScreen(navController)
            }
        }
        composable(AppScreens.BuscaScreen.route) {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Buscar Receitas") })
                },
                bottomBar = { BottomNavigationBar(navController) }
            ) { paddingValues ->
                BuscaScreen(navController)
            }
        }
        composable(AppScreens.ConfiguracoesScreen.route) {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Configurações") })
                },
                bottomBar = { BottomNavigationBar(navController) }
            ) { paddingValues ->
                ConfiguracoesScreen(onBack = { navController.popBackStack() })
            }
        }
        composable(AppScreens.ChatScreen.route) {
            val context = LocalContext.current
            val database = remember { AppDatabase.getDatabase(context) }
            val receitaDao = remember { database.receitaDao() }
            val connectivityObserver = remember { ConnectivityObserver(context) }
            val receitasRepository = remember { 
                ReceitasRepository(
                    receitaDao,
                    database.nutritionDataDao(),
                    connectivityObserver,
                    ImageStorageService(),
                    ErrorHandler()
                ) 
            }
            
            // Obter informações do usuário logado
            val authViewModel: AuthViewModel = viewModel()
            val usuario = authViewModel.usuarioAtual()
            val currentUserId = usuario?.uid
            val currentUserEmail = usuario?.email
            
            val chatViewModel = remember {
                com.example.myapplication.feature.receitas.ChatViewModel(
                    com.example.myapplication.data.GeminiChatService(),
                    receitasRepository,
                    currentUserId,
                    currentUserEmail
                )
            }
            
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Chat com Chef Gemini") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                            }
                        },
                        actions = {
                            IconButton(onClick = { chatViewModel.clearChat() }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Limpar chat")
                            }
                        }
                    )
                },
                bottomBar = { BottomNavigationBar(navController) }
            ) { paddingValues ->
                com.example.myapplication.feature.receitas.ChatScreen(
                    chatViewModel = chatViewModel,
                    onGenerateRecipe = {
                        navController.navigate(AppScreens.TelaInicialScreen.route) {
                            popUpTo(AppScreens.ChatScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        composable(AppScreens.ProfileScreen.route) {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Perfil") })
                },
                bottomBar = { BottomNavigationBar(navController) }
            ) { paddingValues ->
                com.example.myapplication.feature.receitas.ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate(AppScreens.ConfiguracoesScreen.route) }
                )
            }
        }
        composable(
            route = AppScreens.DetalheScreen.routeWithArgs,
            arguments = AppScreens.DetalheScreen.arguments
        ) { backStackEntry ->
            val receitaId = backStackEntry.arguments?.getString("receitaId")
            DetalheScreen(navController = navController, receitaId = receitaId, backStackEntry = backStackEntry)
        }
        // Demais rotas técnicas podem ser acessadas a partir das telas principais
    }
}
