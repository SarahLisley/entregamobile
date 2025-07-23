package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.R
import com.example.myapplication.model.DadosMockados
import com.example.myapplication.model.Receita
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.screens.ReceitasViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TelaInicial(navController: NavHostController, receitasViewModel: ReceitasViewModel = viewModel()) {
    val receitas = receitasViewModel.receitas
    var expandedMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var novoItem by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var itemEditando by remember { mutableStateOf<Receita?>(null) }
    var novoNome by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NutriLivre") },
                actions = {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Favoritos") },
                            onClick = {
                                navController.navigate(AppScreens.FavoritosScreen.route)
                                expandedMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Configurações") },
                            onClick = {
                                navController.navigate(AppScreens.ConfiguracoesScreen.route)
                                expandedMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ajuda") },
                            onClick = {
                                navController.navigate(AppScreens.AjudaScreen.route)
                                expandedMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Lista de Compras") },
                            onClick = {
                                navController.navigate(AppScreens.ShoppingListScreen.route)
                                expandedMenu = false
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar novo item")
            }
        }
    ) { paddingValues ->
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Adicionar novo item") },
                text = {
                    OutlinedTextField(
                        value = novoItem,
                        onValueChange = { novoItem = it },
                        label = { Text("Nome do item") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (novoItem.isNotBlank()) {
                            receitasViewModel.adicionarReceita(novoItem)
                            novoItem = ""
                            showDialog = false
                        }
                    }) {
                        Text("Adicionar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
        if (showEditDialog && itemEditando != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Editar item") },
                text = {
                    OutlinedTextField(
                        value = novoNome,
                        onValueChange = { novoNome = it },
                        label = { Text("Novo nome") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (novoNome.isNotBlank() && itemEditando != null) {
                            receitasViewModel.editarNome(itemEditando!!.id, novoNome)
                            showEditDialog = false
                        }
                    }) {
                        Text("Salvar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showEditDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = receitas, key = { it.id }) { receita ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300)) + slideInVertically(animationSpec = tween(300)) { it / 2 }
                    ) {
                        ReceitaCard(
                            receita = receita,
                            onClick = {
                                navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
                            },
                            onFavoritoClick = { receitasViewModel.toggleFavorito(receita.id) },
                            onEditarClick = {
                                itemEditando = receita
                                novoNome = receita.nome
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReceitaCard(
    receita: Receita,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    onEditarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = receita.imagemUrl,
                contentDescription = receita.nome,
                placeholder = painterResource(com.example.myapplication.R.drawable.placeholder_shimmer),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = receita.nome,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Row {
                    IconButton(onClick = onFavoritoClick) {
                        Icon(
                            imageVector = if (receita.isFavorita) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (receita.isFavorita) "Desfavoritar" else "Favoritar"
                        )
                    }
                    IconButton(onClick = onEditarClick) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar item")
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = receita.descricaoCurta,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}
