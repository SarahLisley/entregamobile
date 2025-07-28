package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.*
import androidx.compose.material3.Text
import com.example.myapplication.ui.screens.ConfiguracoesScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.myapplication.feature.receitas.ChatScreen
import com.example.myapplication.feature.receitas.ChatViewModel
import com.example.myapplication.feature.receitas.ProfileScreen
import com.example.myapplication.core.data.network.GeminiServiceImpl
import com.example.myapplication.core.data.database.AppDatabase
import com.example.myapplication.core.data.network.ConnectivityObserver
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.data.storage.ImageStorageService
import com.example.myapplication.core.ui.error.ErrorHandler
import com.example.myapplication.ui.screens.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// Rotas nomeadas
sealed class AppScreens(val route: String) {
    object TelaInicialScreen : AppScreens("tela_inicial")
    object FavoritosScreen : AppScreens("favoritos")
    object ConfiguracoesScreen : AppScreens("configuracoes")
    object AjudaScreen : AppScreens("ajuda")
    object BuscaScreen : AppScreens("busca")
    object ChatScreen : AppScreens("chat")
    object ProfileScreen : AppScreens("profile")



    object DetalheScreen {
        const val routeWithArgs = "detalhe_receita/{receitaId}?startInEditMode={startInEditMode}"
        const val route = "detalhe_receita"
        val arguments = listOf(
            navArgument("receitaId") { type = NavType.StringType; nullable = true },
            navArgument("startInEditMode") { type = NavType.BoolType; defaultValue = false }
        )

        fun createRoute(receitaId: String?, startInEditMode: Boolean = false): String {
            return "$route/$receitaId?startInEditMode=$startInEditMode"
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.TelaInicialScreen.route
    ) {
        composable(AppScreens.TelaInicialScreen.route) {
            TelaInicial(navController)
        }
        composable(AppScreens.FavoritosScreen.route) {
            FavoritosScreen(navController)
        }
        composable(AppScreens.ConfiguracoesScreen.route) {
            ConfiguracoesScreen(
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }
        composable(AppScreens.AjudaScreen.route) {
            AjudaScreen(navController)
        }
        composable(AppScreens.BuscaScreen.route) {
            BuscaScreen(navController)
        }
        
        composable(AppScreens.ChatScreen.route) {
            val chatViewModel: ChatViewModel = viewModel()
            
            ChatScreen(
                chatViewModel = chatViewModel,
                onGenerateRecipe = {
                    // Navegar para a tela inicial após gerar a receita
                    navController.navigate(AppScreens.TelaInicialScreen.route) {
                        popUpTo(AppScreens.ChatScreen.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(AppScreens.ProfileScreen.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
        


        composable(
            route = AppScreens.DetalheScreen.routeWithArgs,
            arguments = AppScreens.DetalheScreen.arguments
        ) { backStackEntry ->
            val receitaId = backStackEntry.arguments?.getString("receitaId")
            DetalheScreen(navController = navController, receitaId = receitaId, backStackEntry = backStackEntry)
        }
    }
}
