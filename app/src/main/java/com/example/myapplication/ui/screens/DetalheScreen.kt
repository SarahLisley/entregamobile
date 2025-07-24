package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.data.FirebaseRepository
import com.example.myapplication.data.SupabaseImageUploader
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.screens.ReceitasViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.myapplication.ui.screens.ReceitasUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(navController: NavHostController, receitaId: String?) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val receitasViewModel: ReceitasViewModel = viewModel()
    val uiState by receitasViewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
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
    // Buscar receita pelo id no estado do ViewModel
    val receita = (uiState as? ReceitasUiState.Success)?.receitas?.find { it["id"]?.toString() == receitaId }
    val isLoading = uiState is ReceitasUiState.Loading
    val isError = uiState is ReceitasUiState.Error
    LaunchedEffect(receitaId, uiState) {
        if (receita != null && showEditDialog) {
            editNome = receita["nome"] as? String ?: ""
            editDescricao = receita["descricaoCurta"] as? String ?: ""
            editTempo = receita["tempoPreparo"] as? String ?: ""
            editPorcoes = (receita["porcoes"] as? Number)?.toString() ?: ""
            editIngredientes = (receita["ingredientes"] as? List<*>)?.joinToString("\n") ?: ""
            editModoPreparo = (receita["modoPreparo"] as? List<*>)?.joinToString("\n") ?: ""
        }
    }
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
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = {
                        if (receita != null) {
                            val id = receita["id"]?.toString() ?: return@IconButton
                            val imagemUrl = receita["imagemUrl"] as? String ?: ""
                            receitasViewModel.deletarReceita(id, imagemUrl)
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Deletar")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            isError -> {
                val msg = (uiState as ReceitasUiState.Error).message
                LaunchedEffect(msg) { snackbarHostState.showSnackbar(msg) }
            }
            uiState is ReceitasUiState.SuccessMessage -> {
                val msg = (uiState as ReceitasUiState.SuccessMessage).message
                LaunchedEffect(msg) { snackbarHostState.showSnackbar(msg) }
            }
            receita != null -> {
                val r = receita
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    val imagemUrl = r["imagemUrl"] as? String ?: ""
                    if (imagemUrl.isNotBlank()) {
                        AsyncImage(
                            model = imagemUrl,
                            contentDescription = r["nome"] as? String ?: "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = r["nome"] as? String ?: "", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = r["descricaoCurta"] as? String ?: "", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Ingredientes:", style = MaterialTheme.typography.titleMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (r["ingredientes"] as? List<*>)?.forEach { ingrediente ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = ingrediente.toString(),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Modo de Preparo:", style = MaterialTheme.typography.titleMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (r["modoPreparo"] as? List<*>)?.forEachIndexed { index, passo ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(50)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = passo.toString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
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
        // Diálogo de edição (apenas estrutura, lógica de update pode ser implementada no ViewModel futuramente)
        if (showEditDialog && receita != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Editar Receita") },
                text = {
                    Column {
                        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                            Text(if (editImagemUri == null) "Selecionar nova imagem" else "Imagem Selecionada")
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editNome,
                            onValueChange = { editNome = it },
                            label = { Text("Nome da receita") }
                        )
                        OutlinedTextField(
                            value = editDescricao,
                            onValueChange = { editDescricao = it },
                            label = { Text("Descrição curta") }
                        )
                        OutlinedTextField(
                            value = editTempo,
                            onValueChange = { editTempo = it },
                            label = { Text("Tempo de preparo") }
                        )
                        OutlinedTextField(
                            value = editPorcoes,
                            onValueChange = { editPorcoes = it.filter { c -> c.isDigit() } },
                            label = { Text("Porções") }
                        )
                        OutlinedTextField(
                            value = editIngredientes,
                            onValueChange = { editIngredientes = it },
                            label = { Text("Ingredientes (um por linha)") },
                            maxLines = 4
                        )
                        OutlinedTextField(
                            value = editModoPreparo,
                            onValueChange = { editModoPreparo = it },
                            label = { Text("Modo de preparo (um por linha)") },
                            maxLines = 4
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (receita != null) {
                            receitasViewModel.editarReceita(
                                context = context,
                                id = receita["id"]?.toString() ?: return@Button,
                                nome = editNome,
                                descricaoCurta = editDescricao,
                                novaImagemUri = editImagemUri,
                                ingredientes = editIngredientes.split('\n').filter { it.isNotBlank() },
                                modoPreparo = editModoPreparo.split('\n').filter { it.isNotBlank() },
                                tempoPreparo = editTempo,
                                porcoes = editPorcoes.toIntOrNull() ?: 1,
                                imagemUrlAntiga = receita["imagemUrl"] as? String
                            )
                            showEditDialog = false
                        }
                    }) { Text("Salvar") }
                },
                dismissButton = {
                    Button(onClick = { showEditDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}
