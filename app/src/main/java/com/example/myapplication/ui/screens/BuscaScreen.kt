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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myapplication.data.FirebaseRepository
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    val firebaseRepository = remember { FirebaseRepository() }
    var receitasFirebase by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    LaunchedEffect(Unit) {
        firebaseRepository.escutarReceitas { data ->
            receitasFirebase = data?.values?.mapNotNull { it as? Map<String, Any> } ?: emptyList()
        }
    }
    val filteredReceitas = remember(searchText, receitasFirebase) {
        if (searchText.isBlank()) {
            receitasFirebase
        } else {
            receitasFirebase.filter {
                val nome = it["nome"] as? String ?: ""
                val ingredientes = it["ingredientes"] as? List<*> ?: emptyList<String>()
                nome.contains(searchText, ignoreCase = true) ||
                        ingredientes.any { ingrediente ->
                            ingrediente.toString().contains(searchText, ignoreCase = true)
                        }
            }
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
        bottomBar = { BottomNavigationBar(navController = navController) }
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
