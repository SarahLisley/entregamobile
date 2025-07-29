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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.data.SupabaseImageUploader
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.foundation.clickable
import android.util.Log
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Sync
import com.example.myapplication.core.data.network.ImageGenerationService
import com.example.myapplication.feature.receitas.ReceitasViewModel
import com.example.myapplication.ui.screens.ViewModelFactory

import com.example.myapplication.feature.receitas.ReceitasUiState
import java.net.URL
import java.net.HttpURLConnection
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TelaInicial(navController: NavHostController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navBackStackEntry = navController.getBackStackEntry(AppScreens.TelaInicialScreen.route)
    val receitasViewModel: ReceitasViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry,
        factory = ViewModelFactory(context)
    )
    val uiState by receitasViewModel.uiState.collectAsState()
    val recommendedRecipes by receitasViewModel.recommendedRecipes.collectAsState()
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
    var receitaParaDeletar by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTestDialog by remember { mutableStateOf(false) }
    var testRecipeName by remember { mutableStateOf("") }
    // Coleta eventos únicos do ViewModel para exibir Snackbars
    LaunchedEffect(Unit) {
        receitasViewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message = message)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NutriLivre") },
                actions = {
                    // Botão de sincronização
                    IconButton(
                        onClick = { receitasViewModel.syncFromFirebase() }
                    ) {
                        Icon(Icons.Filled.Sync, contentDescription = "Sincronizar do Firebase")
                    }
                    
                    // Botão de teste para a nova funcionalidade
                    IconButton(
                        onClick = { showTestDialog = true }
                    ) {
                        Icon(Icons.Filled.Image, contentDescription = "Testar geração de imagem")
                    }
                    
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
                val msg = (uiState as ReceitasUiState.Error).error.message
                LaunchedEffect(msg) {
                    snackbarHostState.showSnackbar(message = msg)
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
                        // Seção de receitas recomendadas melhorada
                        if (recommendedRecipes.isNotEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Recomendado para Você",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Receitas personalizadas baseadas nas suas preferências",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                            )
                                        }
                                        
                                        TextButton(
                                            onClick = { /* Navegar para mais recomendações */ }
                                        ) {
                                            Text("Ver Mais")
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(recommendedRecipes) { receita ->
                                        EnhancedRecipeCard(
                                            receita = receita,
                                            onClick = {
                                                navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
                                            },
                                            showRecommendationBadge = true
                                        )
                                    }
                                }
                            }
                            
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Todas as Receitas",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                        
                        items(items = receitas, key = { it.id }) { receita ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300)) + slideInVertically(animationSpec = tween(300)) { it / 2 }
                            ) {
                                ReceitaCardFirebase(
                                    receita = receita.toMap(),
                                    onClick = {
                                        navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
                                    },
                                    onEdit = {
                                        // NAVEGAÇÃO PARA EDIÇÃO: Passa o argumento `startInEditMode=true`
                                        navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id, startInEditMode = true))
                                    },
                                    onDelete = {
                                        receitaParaDeletar = receita.toMap()
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
                        Button(onClick = { imagePickerLauncher.launch("image/*") }, enabled = uiState !is ReceitasUiState.Loading) {
                            Text(if (imagemUri == null) "Selecionar Imagem" else "Imagem Selecionada")
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = novoNome,
                            onValueChange = { novoNome = it },
                            label = { Text("Nome da receita") },
                            enabled = uiState !is ReceitasUiState.Loading
                        )
                        OutlinedTextField(
                            value = novaDescricao,
                            onValueChange = { novaDescricao = it },
                            label = { Text("Descrição curta") },
                            enabled = uiState !is ReceitasUiState.Loading
                        )
                        OutlinedTextField(
                            value = novoTempo,
                            onValueChange = { novoTempo = it },
                            label = { Text("Tempo de preparo") },
                            enabled = uiState !is ReceitasUiState.Loading
                        )
                        OutlinedTextField(
                            value = novasPorcoes,
                            onValueChange = { novasPorcoes = it.filter { c -> c.isDigit() } },
                            label = { Text("Porções") },
                            enabled = uiState !is ReceitasUiState.Loading
                        )
                        OutlinedTextField(
                            value = novosIngredientes,
                            onValueChange = { novosIngredientes = it },
                            label = { Text("Ingredientes (um por linha)") },
                            maxLines = 4,
                            enabled = uiState !is ReceitasUiState.Loading
                        )
                        OutlinedTextField(
                            value = novoModoPreparo,
                            onValueChange = { novoModoPreparo = it },
                            label = { Text("Modo de preparo (um por linha)") },
                            maxLines = 4,
                            enabled = uiState !is ReceitasUiState.Loading
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (novoNome.isNotBlank() && imagemUri != null) {
                                receitasViewModel.adicionarReceita(
                                    context = context,
                                    nome = novoNome,
                                    descricaoCurta = novaDescricao,
                                    imagemUri = imagemUri,
                                    ingredientes = novosIngredientes.split('\n').filter { it.isNotBlank() },
                                    modoPreparo = novoModoPreparo.split('\n').filter { it.isNotBlank() },
                                    tempoPreparo = novoTempo,
                                    porcoes = novasPorcoes.toIntOrNull() ?: 1
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
                        },
                        enabled = uiState !is ReceitasUiState.Loading && novoNome.isNotBlank() && imagemUri != null
                    ) {
                        if (uiState is ReceitasUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Adicionar")
                        }
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }, enabled = uiState !is ReceitasUiState.Loading) { Text("Cancelar") }
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
    
    // Diálogo de teste para a nova funcionalidade
    if (showTestDialog) {
        AlertDialog(
            onDismissRequest = { showTestDialog = false },
            title = { Text("🧪 Testar Geração de Imagem") },
            text = {
                Column {
                    Text("Teste a nova funcionalidade de geração de imagens usando o Worker modificado.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = testRecipeName,
                        onValueChange = { testRecipeName = it },
                        label = { Text("Nome da Receita") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                Log.d("TelaInicial", "🧪 INICIANDO TESTE DE GERAÇÃO DE IMAGEM")
                                Log.d("TelaInicial", "📝 Receita de teste: $testRecipeName")
                                
                                val imageService = ImageGenerationService()
                                
                                // Teste de conectividade primeiro
                                Log.d("TelaInicial", "🌐 Testando conectividade...")
                                val isConnected = imageService.testWorkerConnectivity()
                                Log.d("TelaInicial", "📊 Resultado do teste de conectividade: $isConnected")
                                
                                if (isConnected) {
                                    Log.d("TelaInicial", "🎨 Gerando imagem...")
                                    val imageUrl = imageService.generateRecipeImage(testRecipeName)
                                    Log.d("TelaInicial", "✅ Imagem gerada com sucesso!")
                                    Log.d("TelaInicial", "🖼️ URL da imagem: $imageUrl")
                                } else {
                                    Log.e("TelaInicial", "❌ Worker não está acessível")
                                }
                                
                                // Teste simples adicional
                                Log.d("TelaInicial", "🧪 Teste simples do Worker...")
                                val simpleTestResult = imageService.simpleTest()
                                Log.d("TelaInicial", "📊 Resultado do teste simples: $simpleTestResult")
                                
                                // Teste de conectividade básica
                                Log.d("TelaInicial", "🌐 Testando conectividade básica...")
                                try {
                                    val connectivityTest = URL("https://www.google.com").openConnection() as HttpURLConnection
                                    connectivityTest.requestMethod = "HEAD"
                                    connectivityTest.connectTimeout = 5000
                                    val connectivityCode = connectivityTest.responseCode
                                    Log.d("TelaInicial", "✅ Conectividade OK: $connectivityCode")
                                } catch (e: Exception) {
                                    Log.e("TelaInicial", "❌ Problema de conectividade: ${e.message}")
                                }
                            } catch (e: Exception) {
                                Log.e("TelaInicial", "💥 ERRO no teste de geração de imagem")
                                Log.e("TelaInicial", "💥 Tipo da exceção: ${e.javaClass.simpleName}")
                                Log.e("TelaInicial", "💥 Mensagem: ${e.message}")
                                Log.e("TelaInicial", "💥 Stack trace:")
                                e.printStackTrace()
                            }
                        }
                        showTestDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Teste iniciado! Verifique os logs.")
                        }
                    }
                ) {
                    Text("🚀 Testar")
                }
            },
            dismissButton = {
                Button(onClick = { showTestDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EnhancedRecipeCard(
    receita: com.example.myapplication.core.data.database.entity.ReceitaEntity,
    onClick: () -> Unit,
    showRecommendationBadge: Boolean = false
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            Column {
                // Imagem com overlay
                Box {
                    AsyncImage(
                        model = receita.imagemUrl,
                        contentDescription = receita.nome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop,
                        onError = { state ->
                            Log.e("EnhancedRecipeCard", "Erro ao carregar imagem: ${receita.imagemUrl}")
                        },
                        onSuccess = { state ->
                            Log.d("EnhancedRecipeCard", "Imagem carregada com sucesso: ${receita.imagemUrl}")
                        }
                    )
                    
                    // Badge de recomendação
                    if (showRecommendationBadge) {
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.TopStart),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "⭐ Recomendado",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    // Badge de tempo de preparo
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = receita.tempoPreparo,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = receita.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = receita.descricaoCurta,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Tags da receita
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tags = extractRecipeTags(receita)
                        items(tags.take(3)) { tag ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Estatísticas da receita
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${receita.curtidas?.size ?: 0}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${receita.favoritos?.size ?: 0}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${receita.porcoes} porções",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

// Função auxiliar para extrair tags da receita
private fun extractRecipeTags(receita: com.example.myapplication.core.data.database.entity.ReceitaEntity): List<String> {
    val tags = mutableListOf<String>()
    
    // Analisar ingredientes para extrair tags
    receita.ingredientes.forEach { ingrediente ->
        when {
            ingrediente.contains("carne", ignoreCase = true) -> tags.add("Carne")
            ingrediente.contains("frango", ignoreCase = true) -> tags.add("Frango")
            ingrediente.contains("peixe", ignoreCase = true) -> tags.add("Peixe")
            ingrediente.contains("leite", ignoreCase = true) -> tags.add("Lácteo")
            ingrediente.contains("queijo", ignoreCase = true) -> tags.add("Queijo")
            ingrediente.contains("trigo", ignoreCase = true) -> tags.add("Glúten")
            ingrediente.contains("farinha", ignoreCase = true) -> tags.add("Farinha")
            ingrediente.contains("açúcar", ignoreCase = true) -> tags.add("Doce")
            ingrediente.contains("óleo", ignoreCase = true) -> tags.add("Óleo")
            ingrediente.contains("sal", ignoreCase = true) -> tags.add("Salgado")
            ingrediente.contains("ovo", ignoreCase = true) -> tags.add("Ovo")
            ingrediente.contains("legume", ignoreCase = true) -> tags.add("Legume")
            ingrediente.contains("fruta", ignoreCase = true) -> tags.add("Fruta")
            ingrediente.contains("aveia", ignoreCase = true) -> tags.add("Fibras")
            ingrediente.contains("cenoura", ignoreCase = true) -> tags.add("Vitamina A")
            ingrediente.contains("espinafre", ignoreCase = true) -> tags.add("Ferro")
            ingrediente.contains("brócolis", ignoreCase = true) -> tags.add("Vitamina C")
        }
    }
    
    // Adicionar tags baseadas no tempo de preparo
    when {
        receita.tempoPreparo.contains("15") || receita.tempoPreparo.contains("20") -> tags.add("Rápido")
        receita.tempoPreparo.contains("30") || receita.tempoPreparo.contains("45") -> tags.add("Médio")
        else -> tags.add("Elaborado")
    }
    
    // Adicionar tags baseadas no número de porções
    when {
        receita.porcoes <= 2 -> tags.add("Poucas porções")
        receita.porcoes <= 4 -> tags.add("Família")
        else -> tags.add("Grande quantidade")
    }
    
    return tags.distinct().take(3) // Limitar a 3 tags únicas
}

@Composable
fun ReceitaCardFirebase(
    receita: Map<String, Any?>,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCurtir: (id: String, userId: String, curtidas: List<String>) -> Unit,
    onFavoritar: (id: String, userId: String, favoritos: List<String>) -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val usuario = authViewModel.usuarioAtual()
    val userId = usuario?.uid ?: "anon"

    val favoritos = receita["favoritos"] as? List<String> ?: emptyList()
    val curtidas = receita["curtidas"] as? List<String> ?: emptyList()

    val isFavorited by remember(favoritos) { mutableStateOf(favoritos.contains(userId)) }
    val isLiked by remember(curtidas) { mutableStateOf(curtidas.contains(userId)) }
    
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            val imagemUrl = receita["imagemUrl"] as? String ?: ""
            Log.d("ReceitaCardFirebase", "Receita: ${receita["nome"]}, URL da imagem: '$imagemUrl'")
            
            if (imagemUrl.isNotBlank()) {
                Log.d("ReceitaCardFirebase", "Tentando carregar imagem: $imagemUrl")
                AsyncImage(
                    model = imagemUrl,
                    contentDescription = receita["nome"] as? String,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    onError = { state ->
                        Log.e("ReceitaCardFirebase", "Erro ao carregar imagem: ${state.result}")
                        Log.e("ReceitaCardFirebase", "URL da imagem: $imagemUrl")
                        Log.e("ReceitaCardFirebase", "Receita: ${receita["nome"]}")
                    },
                    onSuccess = { state ->
                        Log.d("ReceitaCardFirebase", "Imagem carregada com sucesso: $imagemUrl")
                        Log.d("ReceitaCardFirebase", "Receita: ${receita["nome"]}")
                    }
                )
            } else {
                Log.w("ReceitaCardFirebase", "URL da imagem está vazia para receita: ${receita["nome"]}")
                // Placeholder quando não há imagem
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = "Sem imagem",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = receita["nome"] as? String ?: "Receita sem nome",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = receita["descricaoCurta"] as? String ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Autor e Curtidas
                    Column {
                        val autor = receita["userEmail"] as? String
                        if (!autor.isNullOrBlank()) {
                            Text(
                                text = "por $autor",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ThumbUp,
                                contentDescription = "Curtidas",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${curtidas.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Botões de Ação
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (usuario != null) {
                            IconButton(onClick = {
                                onCurtir(receita["id"].toString(), userId, curtidas)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ThumbUp,
                                    contentDescription = "Curtir",
                                    tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            IconButton(onClick = {
                                onFavoritar(receita["id"].toString(), userId, favoritos)
                            }) {
                                Icon(
                                    imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favoritar",
                                    tint = if (isFavorited) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }

                        // Ações do proprietário da receita
                        if (usuario?.uid == receita["userId"] as? String) {
                            IconButton(onClick = onEdit) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Filled.Delete, contentDescription = "Deletar")
                            }
                        }
                    }
                }
            }
        }
    }
}
