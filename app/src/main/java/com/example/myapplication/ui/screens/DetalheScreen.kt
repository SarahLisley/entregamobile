package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.model.DadosMockados
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.screens.ReceitasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(navController: NavHostController, receitaId: Int?, receitasViewModel: ReceitasViewModel = viewModel()) {
    val receita = remember { DadosMockados.listaDeReceitas.find { it.id == receitaId } }
    var isFavorite by remember { mutableStateOf(receita?.isFavorita ?: false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
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
        receita?.let { r ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = r.imagemUrl,
                    contentDescription = r.nome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = r.nome, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = r.descricaoCurta, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Ingredientes:", style = MaterialTheme.typography.titleMedium)
                r.ingredientes.forEach { ingrediente ->
                    Text(text = "- $ingrediente")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Modo de Preparo:", style = MaterialTheme.typography.titleMedium)
                r.modoPreparo.forEachIndexed { index, passo ->
                    Text(text = "${index + 1}. $passo")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        r.isFavorita = !r.isFavorita
                        isFavorite = r.isFavorita
                        if (isFavorite) {
                            DadosMockados.listaDeFavoritosMock.add(r)
                        } else {
                            DadosMockados.listaDeFavoritosMock.remove(r)
                        }
                    }) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Favoritar",
                            tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isFavorite) "Remover dos Favoritos" else "Adicionar aos Favoritos")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { }, enabled = false) {
                        Text("Ouvir/Ver")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Receitas Relacionadas:", style = MaterialTheme.typography.titleMedium)
                LazyRow {
                    items(DadosMockados.listaDeReceitas.take(3)) { relatedReceita ->
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .padding(end = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                AsyncImage(
                                    model = relatedReceita.imagemUrl,
                                    contentDescription = relatedReceita.nome,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(relatedReceita.nome, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Receita não encontrada.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
