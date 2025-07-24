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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(navController: NavHostController) {
    val firebaseRepository = remember { FirebaseRepository() }
    var favoritos by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        firebaseRepository.escutarReceitas { data ->
            favoritos = data?.values?.mapNotNull {
                val receita = it as? Map<String, Any>
                if (receita != null && (receita["isFavorita"] as? Boolean == true)) receita else null
            } ?: emptyList()
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
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
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
                    Card(modifier = Modifier.fillMaxWidth().clickable {
                        val id = receita["id"]?.toString() ?: ""
                        navController.navigate(AppScreens.DetalheScreen.createRoute(id))
                    }, elevation = CardDefaults.cardElevation(4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val imagemUrl = receita["imagemUrl"] as? String ?: ""
                            if (imagemUrl.isNotBlank()) {
                                AsyncImage(
                                    model = imagemUrl,
                                    contentDescription = receita["nome"] as? String ?: "",
                                    modifier = Modifier.fillMaxWidth().height(120.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = receita["nome"] as? String ?: "", style = MaterialTheme.typography.headlineLarge)
                            Text(text = receita["descricaoCurta"] as? String ?: "", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
