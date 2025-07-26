package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.*
import androidx.compose.material3.Text
import com.example.myapplication.ui.screens.ConfiguracoesScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

// Rotas nomeadas
sealed class AppScreens(val route: String) {
    object TelaInicialScreen : AppScreens("tela_inicial")
    object FavoritosScreen : AppScreens("favoritos")
    object ConfiguracoesScreen : AppScreens("configuracoes")
    object AjudaScreen : AppScreens("ajuda")
    object BuscaScreen : AppScreens("busca")


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
            ConfiguracoesScreen(onBack = { navController.popBackStack() })
        }
        composable(AppScreens.AjudaScreen.route) {
            AjudaScreen(navController)
        }
        composable(AppScreens.BuscaScreen.route) {
            BuscaScreen(navController)
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
