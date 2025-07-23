package com.example.myapplication.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.ShoppingItem

// Anotação para os componentes experimentais do Material 3 que usamos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingListViewModel = viewModel()) {
    val shoppingItems by viewModel.items.collectAsState(initial = emptyList())

    // Estado centralizado para controlar o diálogo
    var showDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ShoppingItem?>(null) }

    // O Scaffold é a estrutura principal da tela
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Compras") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                itemToEdit = null // Define como nulo para indicar o modo "Adicionar"
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Item")
            }
        }
    ) { paddingValues ->
        // LazyColumn para exibir a lista de itens com eficiência
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {
            items(shoppingItems, key = { item -> item.id }) { item ->
                ShoppingListItem(
                    item = item,
                    onEdit = {
                        itemToEdit = item // Define o item para indicar o modo "Editar"
                        showDialog = true
                    },
                    onDelete = { viewModel.deleteItem(item) }
                )
            }
        }
    }

    // Lógica para exibir o diálogo de Adicionar/Editar
    if (showDialog) {
        AddOrEditItemDialog(
            item = itemToEdit,
            onDismiss = { showDialog = false },
            onConfirm = { name, quantity ->
                if (itemToEdit == null) {
                    // Adicionar novo item
                    viewModel.addItem(ShoppingItem(nome = name, quantidade = quantity))
                } else {
                    // Atualizar item existente
                    val updatedItem = itemToEdit!!.copy(nome = name, quantidade = quantity)
                    viewModel.updateItem(updatedItem)
                }
                showDialog = false
            }
        )
    }
}

// Composable para o item da lista com a funcionalidade de deslizar para excluir
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingListItem(
    item: ShoppingItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true // Confirma a mudança de estado
            } else {
                false // Não confirma para outras direções
            }
        },
        positionalThreshold = { it * 0.25f } // Deslizar 25% para acionar
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.padding(vertical = 4.dp),
        enableDismissFromStartToEnd = false, // Desabilita deslizar da esquerda para a direita
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color.Red.copy(alpha = 0.8f) else Color.LightGray, label = ""
            )
            val scale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 0.8f, label = ""
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Excluir",
                    modifier = Modifier.scale(scale),
                    tint = Color.White
                )
            }
        }
    ) {
        // Conteúdo do item da lista
        ListItem(
            headlineContent = { Text(item.nome, style = MaterialTheme.typography.titleMedium) },
            supportingContent = { Text("Quantidade: ${item.quantidade}") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onEdit)
        )
    }
}

// Composable reutilizável para o diálogo de Adicionar e Editar
@Composable
private fun AddOrEditItemDialog(
    item: ShoppingItem?,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf(item?.nome ?: "") }
    var quantity by remember { mutableStateOf(item?.quantidade?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Adicionar Item" else "Editar Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Item") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { newValue ->
                        // Permite apenas dígitos
                        if (newValue.all { it.isDigit() }) {
                            quantity = newValue
                        }
                    },
                    label = { Text("Quantidade") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val quantityInt = quantity.toIntOrNull() ?: 1
                    if (name.isNotBlank()) {
                        onConfirm(name, quantityInt)
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}