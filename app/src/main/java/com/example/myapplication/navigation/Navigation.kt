package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.*
import androidx.compose.material3.Text // <- Importa o Text correto do Material3
import com.example.myapplication.ui.screens.ConfiguracoesScreen

// Rotas nomeadas
sealed class AppScreens(val route: String) {
    object TelaInicialScreen : AppScreens("tela_inicial")
    object FavoritosScreen : AppScreens("favoritos")
    object ConfiguracoesScreen : AppScreens("configuracoes")
    object AjudaScreen : AppScreens("ajuda")
    object BuscaScreen : AppScreens("busca")
    object ShoppingListScreen : AppScreens("shopping_list")

    object DetalheScreen : AppScreens("detalhe_receita/{receitaId}") {
        fun createRoute(receitaId: Int): String {
            return "detalhe_receita/$receitaId"
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
            ConfiguracoesScreen(onBack = { navController.popBackStack() })
        }
        composable(AppScreens.AjudaScreen.route) {
            AjudaScreen(navController)
        }
        composable(AppScreens.BuscaScreen.route) {
            BuscaScreen(navController)
        }
        composable(AppScreens.ShoppingListScreen.route) {
            ShoppingListScreen()
        }
        composable(AppScreens.DetalheScreen.route) { backStackEntry ->
            val receitaId = backStackEntry.arguments?.getString("receitaId")?.toIntOrNull()
            if (receitaId != null) {
                DetalheScreen(navController = navController, receitaId = receitaId)
            } else {
                // Fallback seguro em caso de ID inválido
                Text("Erro: receitaId inválido ou ausente")
            }
        }
    }
}
