// app/src/main/java/com/example/myapplication/ui/components/BottomNavigationBar.kt
package com.example.myapplication.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.navigation.AppScreens

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Receitas") },
            label = { Text("Receitas") },
            selected = currentRoute == AppScreens.TelaInicialScreen.route,
            onClick = { 
                if (currentRoute != AppScreens.TelaInicialScreen.route) {
                    navController.navigate(AppScreens.TelaInicialScreen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            label = { Text("Buscar") },
            selected = currentRoute == AppScreens.BuscaScreen.route,
            onClick = { 
                if (currentRoute != AppScreens.BuscaScreen.route) {
                    navController.navigate(AppScreens.BuscaScreen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favoritos") },
            selected = currentRoute == AppScreens.FavoritosScreen.route,
            onClick = { 
                if (currentRoute != AppScreens.FavoritosScreen.route) {
                    navController.navigate(AppScreens.FavoritosScreen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Configurações") },
            label = { Text("Configurações") },
            selected = currentRoute == AppScreens.ConfiguracoesScreen.route,
            onClick = { 
                if (currentRoute != AppScreens.ConfiguracoesScreen.route) {
                    navController.navigate(AppScreens.ConfiguracoesScreen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}
