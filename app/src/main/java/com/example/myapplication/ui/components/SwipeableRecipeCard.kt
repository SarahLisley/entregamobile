package com.example.myapplication.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.data.database.entity.ReceitaEntity

/**
 * Card de receita com funcionalidade de swipe para favoritar
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SwipeableRecipeCard(
    receita: ReceitaEntity,
    onSwipeToFavorite: () -> Unit,
    onCardClick: () -> Unit,
    onFavoriteClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToEnd) {
                onSwipeToFavorite()
                false // Não descarta o item
            } else {
                false
            }
        }
    )
    
    // Animação do ícone de favorito
    val scale by animateFloatAsState(
        targetValue = if (dismissState.targetValue == DismissValue.DismissedToEnd) 1.2f else 1f,
        label = "scale"
    )
    
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        when (dismissState.targetValue) {
                            DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
                            else -> Color.Transparent
                        }
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favoritar",
                    modifier = Modifier
                        .scale(scale)
                        .size(32.dp),
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            ReceitaCard(
                receita = receita,
                onClick = onCardClick,
                onFavoriteClick = onFavoriteClick,
                modifier = modifier
            )
        }
    )
} 