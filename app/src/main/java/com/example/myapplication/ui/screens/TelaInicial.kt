package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.ThumbUp
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myapplication.navigation.AppScreens
import com.example.myapplication.ui.components.BottomNavigationBar
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Delete
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.data.FirebaseRepository
import com.example.myapplication.data.SupabaseImageUploader
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TelaInicial(navController: NavHostController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val receitasViewModel: ReceitasViewModel = viewModel()
    val uiState by receitasViewModel.uiState.collectAsState()
    val authViewModel: AuthViewModel = viewModel()
    val usuario = authViewModel.usuarioAtual()
    val isUsuarioLogado = usuario != null
    var expandedMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var novoNome by remember { mutableStateOf("") }
    var novaDescricao by remember { mutableStateOf("") }
    var novoTempo by remember { mutableStateOf("") }
    var novasPorcoes by remember { mutableStateOf("") }
    var novosIngredientes by remember { mutableStateOf("") }
    var novoModoPreparo by remember { mutableStateOf("") }
    var imagemUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) imagemUri = uri
    }
    var receitaParaDeletar by remember { mutableStateOf<Map<String, Any>?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
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
            FloatingActionButton(
                onClick = { if (isUsuarioLogado) showDialog = true },
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar novo item")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (uiState) {
            is ReceitasUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ReceitasUiState.Error -> {
                val msg = (uiState as ReceitasUiState.Error).message
                LaunchedEffect(msg) {
                    snackbarHostState.showSnackbar(msg)
                }
            }
            is ReceitasUiState.SuccessMessage -> {
                val msg = (uiState as ReceitasUiState.SuccessMessage).message
                LaunchedEffect(msg) {
                    snackbarHostState.showSnackbar(msg)
                }
            }
            is ReceitasUiState.Success -> {
                val receitas = (uiState as ReceitasUiState.Success).receitas
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
                        items(items = receitas, key = { it["id"] as? String ?: it["id"].toString() }) { receita ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300)) + slideInVertically(animationSpec = tween(300)) { it / 2 }
                            ) {
                                ReceitaCardFirebase(
                                    receita = receita,
                                    onClick = {
                                        val id = receita["id"]?.toString() ?: ""
                                        navController.navigate(AppScreens.DetalheScreen.createRoute(id))
                                    },
                                    onEdit = {}, // edição não implementada
                                    onDelete = {
                                        receitaParaDeletar = receita
                                        showDeleteDialog = true
                                    },
                                    onCurtir = { id, userId, curtidas ->
                                        receitasViewModel.curtirReceita(id, userId, curtidas)
                                    },
                                    onFavoritar = { id, userId, favoritos ->
                                        receitasViewModel.favoritarReceita(id, userId, favoritos)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Adicionar nova receita") },
                text = {
                    Column {
                        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                            Text(if (imagemUri == null) "Selecionar Imagem" else "Imagem Selecionada")
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = novoNome,
                            onValueChange = { novoNome = it },
                            label = { Text("Nome da receita") }
                        )
                        OutlinedTextField(
                            value = novaDescricao,
                            onValueChange = { novaDescricao = it },
                            label = { Text("Descrição curta") }
                        )
                        OutlinedTextField(
                            value = novoTempo,
                            onValueChange = { novoTempo = it },
                            label = { Text("Tempo de preparo") }
                        )
                        OutlinedTextField(
                            value = novasPorcoes,
                            onValueChange = { novasPorcoes = it.filter { c -> c.isDigit() } },
                            label = { Text("Porções") }
                        )
                        OutlinedTextField(
                            value = novosIngredientes,
                            onValueChange = { novosIngredientes = it },
                            label = { Text("Ingredientes (um por linha)") },
                            maxLines = 4
                        )
                        OutlinedTextField(
                            value = novoModoPreparo,
                            onValueChange = { novoModoPreparo = it },
                            label = { Text("Modo de preparo (um por linha)") },
                            maxLines = 4
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (novoNome.isNotBlank() && imagemUri != null) {
                            receitasViewModel.adicionarReceita(
                                context = context,
                                nome = novoNome,
                                descricaoCurta = novaDescricao,
                                imagemUri = imagemUri,
                                ingredientes = novosIngredientes.split('\n').filter { it.isNotBlank() },
                                modoPreparo = novoModoPreparo.split('\n').filter { it.isNotBlank() },
                                tempoPreparo = novoTempo,
                                porcoes = novasPorcoes.toIntOrNull() ?: 1,
                                userId = usuario?.uid ?: "anon",
                                userEmail = usuario?.email ?: ""
                            )
                            novoNome = ""
                            novaDescricao = ""
                            novoTempo = ""
                            novasPorcoes = ""
                            novosIngredientes = ""
                            novoModoPreparo = ""
                            imagemUri = null
                            showDialog = false
                        }
                    }) { Text("Adicionar") }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) { Text("Cancelar") }
                }
            )
        }
        if (showDeleteDialog && receitaParaDeletar != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false; receitaParaDeletar = null },
                title = { Text("Deletar receita") },
                text = { Text("Tem certeza que deseja deletar esta receita?") },
                confirmButton = {
                    Button(onClick = {
                        val id = receitaParaDeletar?.get("id")?.toString() ?: return@Button
                        val imagemUrl = receitaParaDeletar?.get("imagemUrl") as? String ?: ""
                        receitasViewModel.deletarReceita(id, imagemUrl)
                        showDeleteDialog = false
                        receitaParaDeletar = null
                    }) { Text("Deletar") }
                },
                dismissButton = {
                    Button(onClick = { showDeleteDialog = false; receitaParaDeletar = null }) { Text("Cancelar") }
                }
            )
        }
    }
}

// Remover ReceitaCard, lógica de edição/favorito local e dependências de ReceitasViewModel
// Deixar apenas ReceitaCardFirebase e fluxo 100% Firebase
@Composable
fun ReceitaCardFirebase(
    receita: Map<String, Any>,
    onClick: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onCurtir: (id: String, userId: String, curtidas: List<String>) -> Unit = { _, _, _ -> },
    onFavoritar: (id: String, userId: String, favoritos: List<String>) -> Unit = { _, _, _ -> }
) {
    val firebaseRepository = remember { FirebaseRepository() }
    val authViewModel: AuthViewModel = viewModel()
    val usuario = authViewModel.usuarioAtual()
    val isUsuarioLogado = usuario != null
    val userId = usuario?.uid ?: "anon"
    val favoritos = receita["favoritos"] as? List<String> ?: emptyList()
    val curtidas = receita["curtidas"] as? List<String> ?: emptyList()
    var isFavorite by remember { mutableStateOf(favoritos.contains(userId)) }
    var isLiked by remember { mutableStateOf(curtidas.contains(userId)) }
    val qtdCurtidas = curtidas.size
    val qtdFavoritos = favoritos.size
    val scope = rememberCoroutineScope()
    var likeScale by remember { mutableStateOf(1f) }
    var favScale by remember { mutableStateOf(1f) }
    val animatedLikeScale by animateFloatAsState(targetValue = likeScale, animationSpec = spring())
    val animatedFavScale by animateFloatAsState(targetValue = favScale, animationSpec = spring())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            val imagemUrl = receita["imagemUrl"] as? String ?: ""
            if (imagemUrl.isNotBlank()) {
                AsyncImage(
                    model = imagemUrl,
                    contentDescription = receita["nome"] as? String ?: "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = receita["nome"] as? String ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = receita["descricaoCurta"] as? String ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(8.dp))
            // Badge de kcal (se existir)
            val kcal = receita["kcal"] as? String
            if (!kcal.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$kcal kcal",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.padding(end = 8.dp)) {
                    val autor = receita["userEmail"] as? String ?: ""
                    if (autor.isNotBlank()) {
                        Text(
                            text = "por $autor",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ThumbUp,
                            contentDescription = "Curtidas",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                        Text("$qtdCurtidas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                Row {
                    IconButton(
                        onClick = {
                            if (!isUsuarioLogado) return@IconButton
                            val id = receita["id"]?.toString() ?: return@IconButton
                            val newLike = !isLiked
                            isLiked = newLike
                            likeScale = 1.2f
                            scope.launch {
                                onCurtir(id, userId, curtidas)
                                likeScale = 1f
                            }
                        },
                        enabled = isUsuarioLogado,
                        modifier = Modifier.scale(animatedLikeScale)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ThumbUp,
                            contentDescription = if (isLiked) "Descurtir" else "Curtir",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(
                        onClick = {
                            if (!isUsuarioLogado) return@IconButton
                            val id = receita["id"]?.toString() ?: return@IconButton
                            val newFav = !isFavorite
                            isFavorite = newFav
                            favScale = 1.2f
                            scope.launch {
                                onFavoritar(id, userId, favoritos)
                                favScale = 1f
                            }
                        },
                        enabled = isUsuarioLogado,
                        modifier = Modifier.scale(animatedFavScale)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = if (isFavorite) "Desfavoritar" else "Favoritar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    val autorId = receita["userId"] as? String ?: ""
                    if (autorId == userId) {
                        IconButton(onClick = { onEdit() }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Editar receita"
                            )
                        }
                        IconButton(onClick = { onDelete() }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Deletar receita"
                            )
                        }
                    }
                }
            }
        }
    }
}
