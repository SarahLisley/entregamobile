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
import com.example.myapplication.model.Produto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoScreen(viewModel: ProdutoViewModel = viewModel()) {
    val produtos by viewModel.produtos.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.buscarProdutos()
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Adicionar Produto") },
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
                        viewModel.adicionarProduto(Produto(id = 0, nome = nome, preco = precoDouble))
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
            TopAppBar(title = { Text("Produtos (API)") })
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
                    }
                }
            }
        }
    }
}
