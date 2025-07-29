package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.ui.components.SwipeableRecipeCard
import kotlinx.coroutines.flow.Flow

/**
 * Componente de lista paginada para receitas
 * 
 * @param receitasPagingData Flow de dados paginados
 * @param onReceitaClick Callback quando uma receita é clicada
 * @param onReceitaFavorite Callback quando uma receita é favoritada
 * @param modifier Modificador do componente
 * @param showLoadingIndicator Se deve mostrar indicador de carregamento
 * @param showErrorIndicator Se deve mostrar indicador de erro
 * @param emptyMessage Mensagem quando não há receitas
 */
@Composable
fun ReceitasPaginatedList(
    receitasPagingData: Flow<PagingData<ReceitaEntity>>,
    onReceitaClick: (ReceitaEntity) -> Unit,
    onReceitaFavorite: (ReceitaEntity, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    showLoadingIndicator: Boolean = true,
    showErrorIndicator: Boolean = true,
    emptyMessage: String = "Nenhuma receita encontrada"
) {
    val receitas = receitasPagingData.collectAsLazyPagingItems()
    
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = receitas.itemCount,
            key = { index ->
                val receita = receitas[index]
                receita?.id ?: index
            }
        ) { index ->
            val receita = receitas[index]
            if (receita != null) {
                SwipeableRecipeCard(
                    receita = receita,
                    onSwipeToFavorite = {
                        onReceitaFavorite(receita, true)
                    },
                    onCardClick = { onReceitaClick(receita) },
                    onFavoriteClick = { isFavorite ->
                        onReceitaFavorite(receita, isFavorite)
                    }
                )
            }
        }
        
        // Indicador de carregamento
        if (receitas.loadState.append is androidx.paging.LoadState.Loading && showLoadingIndicator) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
        
        // Indicador de erro
        if (receitas.loadState.append is androidx.paging.LoadState.Error && showErrorIndicator) {
            item {
                val error = (receitas.loadState.append as androidx.paging.LoadState.Error).error
                ErrorItem(
                    message = "Erro ao carregar mais receitas: ${error.message}",
                    onRetry = { receitas.retry() }
                )
            }
        }
        
        // Mensagem de lista vazia
        if (receitas.loadState.refresh is androidx.paging.LoadState.NotLoading && 
            receitas.itemCount == 0) {
            item {
                EmptyState(message = emptyMessage)
            }
        }
    }
}

/**
 * Componente para mostrar estado de erro
 */
@Composable
private fun ErrorItem(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Tentar Novamente")
        }
    }
}

/**
 * Componente para mostrar estado vazio
 */
@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Componente de lista paginada com busca
 */
@Composable
fun ReceitasPaginatedListWithSearch(
    receitasPagingData: Flow<PagingData<ReceitaEntity>>,
    searchQuery: String,
    onReceitaClick: (ReceitaEntity) -> Unit,
    onReceitaFavorite: (ReceitaEntity, Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Campo de busca
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Buscar receitas") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        
        // Lista paginada
        ReceitasPaginatedList(
            receitasPagingData = receitasPagingData,
            onReceitaClick = onReceitaClick,
            onReceitaFavorite = onReceitaFavorite,
            modifier = Modifier.fillMaxSize(),
            emptyMessage = if (searchQuery.isBlank()) {
                "Nenhuma receita encontrada"
            } else {
                "Nenhuma receita encontrada para '$searchQuery'"
            }
        )
    }
} 