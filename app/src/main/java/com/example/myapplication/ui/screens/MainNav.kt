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
