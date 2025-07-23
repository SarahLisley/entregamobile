package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.ProdutoRepository
import com.example.myapplication.data.ProdutoApiService
import com.example.myapplication.model.ProdutoEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProdutosOfflineViewModel(
    private val repository: ProdutoRepository,
    private val dao: com.example.myapplication.data.ProdutoDao
) : ViewModel() {
    private val _produtos = MutableStateFlow<List<ProdutoEntity>>(emptyList())
    val produtos: StateFlow<List<ProdutoEntity>> = _produtos

    init {
        viewModelScope.launch {
            dao.getAll().collectLatest { _produtos.value = it }
        }
    }

    fun addProduto(nome: String, preco: Double) {
        repository.addProduto(nome, preco)
    }

    fun sincronizar() {
        repository.tentarSincronizar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutosOfflineScreen(
    context: android.content.Context = androidx.compose.ui.platform.LocalContext.current
) {
    // Instanciar dependências manualmente (sem DI)
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { ProdutoRepository(context, db.produtoDao(), ProdutoApiServiceInstance.api) }
    val viewModel: ProdutosOfflineViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProdutosOfflineViewModel(repository, db.produtoDao()) as T
            }
        }
    )
    val produtos by viewModel.produtos.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Adicionar Produto (Offline)") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome") }
                    )
                    OutlinedTextField(
                        value = preco,
                        onValueChange = { preco = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Preço") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val precoDouble = preco.toDoubleOrNull() ?: 0.0
                    if (nome.isNotBlank()) {
                        viewModel.addProduto(nome, precoDouble)
                        nome = ""
                        preco = ""
                        showAddDialog = false
                    }
                }) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                Button(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Produtos Offline (Room)") }, actions = {
                Button(onClick = { viewModel.sincronizar() }) {
                    Text("Sincronizar")
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(produtos) { produto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(produto.nome, style = MaterialTheme.typography.titleMedium)
                        Text("Preço: R$ ${produto.preco}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            if (produto.sincronizado) "Sincronizado" else "Pendente de sincronização",
                            color = if (produto.sincronizado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

// Instância singleton para ProdutoApiService
object ProdutoApiServiceInstance {
    val api: com.example.myapplication.data.ProdutoApiService by lazy {
        com.example.myapplication.data.RetrofitInstance.api
    }
}
