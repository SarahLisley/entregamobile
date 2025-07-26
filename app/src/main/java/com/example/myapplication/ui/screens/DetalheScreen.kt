package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.myapplication.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(
    navController: NavHostController,
    receitaId: String?,
    backStackEntry: NavBackStackEntry // Recebe o backStackEntry para ler os argumentos
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // CORREÇÃO: Compartilha o ViewModel com a TelaInicial
    val navBackStackEntry = navController.getBackStackEntry(AppScreens.TelaInicialScreen.route)
    val receitasViewModel: ReceitasViewModel = viewModel(viewModelStoreOwner = navBackStackEntry)

    val uiState by receitasViewModel.uiState.collectAsState()

    // LÓGICA DE EDIÇÃO: Lê o argumento de navegação
    val startInEditMode = backStackEntry.arguments?.getBoolean("startInEditMode") ?: false
    var showEditDialog by remember(startInEditMode) { mutableStateOf(startInEditMode) }

    var editNome by remember { mutableStateOf("") }
    var editDescricao by remember { mutableStateOf("") }
    var editTempo by remember { mutableStateOf("") }
    var editPorcoes by remember { mutableStateOf("") }
    var editIngredientes by remember { mutableStateOf("") }
    var editModoPreparo by remember { mutableStateOf("") }
    var editImagemUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) editImagemUri = uri
    }

    val receita = (uiState as? ReceitasUiState.Success)?.receitas?.find { it.id == receitaId }
    val isLoading = uiState is ReceitasUiState.Loading
    val isError = uiState is ReceitasUiState.Error
    
    // Estado das informações nutricionais
    val nutritionState by receitasViewModel.nutritionState.collectAsState()

    LaunchedEffect(receita, showEditDialog) {
        if (receita != null && showEditDialog) {
            editNome = receita.nome
            editDescricao = receita.descricaoCurta
            editTempo = receita.tempoPreparo
            editPorcoes = receita.porcoes.toString()
            editIngredientes = receita.ingredientes.joinToString("\n")
            editModoPreparo = receita.modoPreparo.joinToString("\n")
            editImagemUri = null // Reseta a imagem ao abrir o diálogo
        }
    }

    LaunchedEffect(Unit) {
        receitasViewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message = message)
        }
    }
    
    // Limpar informações nutricionais ao sair da tela
    DisposableEffect(Unit) {
        onDispose {
            receitasViewModel.limparInformacoesNutricionais()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(receita?.nome ?: "Detalhes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (receita != null) {
                        IconButton(onClick = { 
                            receitasViewModel.buscarInformacoesNutricionais(receita.nome)
                        }) {
                            Icon(Icons.Filled.Info, contentDescription = "Informações Nutricionais")
                        }
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = {
                            receitasViewModel.deletarReceita(receita.id, receita.imagemUrl)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Deletar")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            isLoading && receita == null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            isError -> {
                val msg = (uiState as ReceitasUiState.Error).message
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Erro ao carregar: $msg")
                }
            }
            receita != null -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    val imagemUrl = receita.imagemUrl
                    if (imagemUrl.isNotBlank()) {
                        AsyncImage(
                            model = imagemUrl,
                            contentDescription = receita.nome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = receita.nome, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = receita.descricaoCurta, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Ingredientes:", style = MaterialTheme.typography.titleMedium)
                    receita.ingredientes.forEach { ingrediente ->
                        Text("• $ingrediente", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Modo de Preparo:", style = MaterialTheme.typography.titleMedium)
                    receita.modoPreparo.forEachIndexed { index, passo ->
                        Text("${index + 1}. $passo", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                    }
                    
                    // Informações Nutricionais
                    if (nutritionState != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Informações Nutricionais:", style = MaterialTheme.typography.titleMedium)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Calorias:", style = MaterialTheme.typography.bodyMedium)
                                    Text("${nutritionState!!.calories.toInt()} kcal", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Proteínas:", style = MaterialTheme.typography.bodyMedium)
                                    Text("${nutritionState!!.protein.toInt()}g", style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Gorduras:", style = MaterialTheme.typography.bodyMedium)
                                    Text("${nutritionState!!.fat.toInt()}g", style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Carboidratos:", style = MaterialTheme.typography.bodyMedium)
                                    Text("${nutritionState!!.carbohydrates.toInt()}g", style = MaterialTheme.typography.bodyMedium)
                                }
                                nutritionState!!.fiber?.let { fiber ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Fibras:", style = MaterialTheme.typography.bodyMedium)
                                        Text("${fiber.toInt()}g", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                nutritionState!!.sugar?.let { sugar ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Açúcares:", style = MaterialTheme.typography.bodyMedium)
                                        Text("${sugar.toInt()}g", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { Text("Receita não encontrada.", style = MaterialTheme.typography.bodyLarge) }
        }

        if (showEditDialog && receita != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Editar Receita") },
                text = {
                     Column {
                        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                            Text(if (editImagemUri == null) "Selecionar nova imagem" else "Nova Imagem Selecionada!")
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = editNome, onValueChange = { editNome = it }, label = { Text("Nome da receita") })
                        OutlinedTextField(value = editDescricao, onValueChange = { editDescricao = it }, label = { Text("Descrição curta") })
                        OutlinedTextField(value = editTempo, onValueChange = { editTempo = it }, label = { Text("Tempo de preparo") })
                        OutlinedTextField(value = editPorcoes, onValueChange = { editPorcoes = it.filter { c -> c.isDigit() } }, label = { Text("Porções") })
                        OutlinedTextField(value = editIngredientes, onValueChange = { editIngredientes = it }, label = { Text("Ingredientes (um por linha)") }, maxLines = 4)
                        OutlinedTextField(value = editModoPreparo, onValueChange = { editModoPreparo = it }, label = { Text("Modo de preparo (um por linha)") }, maxLines = 4)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            receitasViewModel.editarReceita(
                                context = context,
                                id = receita.id,
                                nome = editNome,
                                descricaoCurta = editDescricao,
                                novaImagemUri = editImagemUri,
                                ingredientes = editIngredientes.split('\n').filter { it.isNotBlank() },
                                modoPreparo = editModoPreparo.split('\n').filter { it.isNotBlank() },
                                tempoPreparo = editTempo,
                                porcoes = editPorcoes.toIntOrNull() ?: 1,
                                imagemUrlAntiga = receita.imagemUrl
                            )
                            showEditDialog = false
                        },
                        enabled = editNome.isNotBlank() && editDescricao.isNotBlank()
                    ) { Text("Salvar") }
                },
                dismissButton = { Button(onClick = { showEditDialog = false }) { Text("Cancelar") } }
            )
        }
    }
}
