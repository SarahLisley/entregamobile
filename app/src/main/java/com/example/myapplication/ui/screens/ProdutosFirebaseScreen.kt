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
import com.example.myapplication.data.FirebaseRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutosFirebaseScreen(
    authViewModel: AuthViewModel = viewModel(),
    repository: FirebaseRepository = FirebaseRepository()
) {
    val usuario = authViewModel.usuarioAtual()
    var produtos by remember { mutableStateOf<Map<String, Any>?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Escuta em tempo real
    LaunchedEffect(Unit) {
        repository.escutarProdutos { data ->
            produtos = data
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Adicionar Produto (Realtime DB)") },
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
                    if (nome.isNotBlank() && usuario != null) {
                        scope.launch {
                            repository.salvarProduto(
                                id = System.currentTimeMillis().toString(),
                                nome = nome,
                                preco = precoDouble
                            )
                            nome = ""
                            preco = ""
                            showAddDialog = false
                        }
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
            TopAppBar(title = { Text("Produtos (Realtime DB)") })
        },
        floatingActionButton = {
            if (usuario != null) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Text("+")
                }
            }
        }
    ) { paddingValues ->
        if (usuario == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Faça login para ver e adicionar produtos.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                produtos?.entries?.let { entries ->
                    items(entries.toList()) { (id, value) ->
                        val produto = value as? Map<*, *>
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(produto?.get("nome")?.toString() ?: "", style = MaterialTheme.typography.titleMedium)
                                Text("Preço: R$ ${produto?.get("preco")}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
