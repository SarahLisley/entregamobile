package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.R

/**
 * Componente de imagem com cache inteligente usando Coil
 * 
 * @param url URL da imagem
 * @param contentDescription Descrição da imagem para acessibilidade
 * @param modifier Modificador do componente
 * @param contentScale Escala de conteúdo da imagem
 * @param shape Forma da imagem (padrão: RectangleShape)
 * @param size Tamanho da imagem (opcional)
 * @param showPlaceholder Se deve mostrar placeholder durante carregamento
 * @param showError Se deve mostrar ícone de erro quando falhar
 */
@Composable
fun CachedImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    size: Dp? = null,
    showPlaceholder: Boolean = true,
    showError: Boolean = true
) {
    val context = LocalContext.current
    
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .memoryCacheKey(url)
            .diskCacheKey(url)
            .crossfade(true)
            .crossfade(300)
            .build(),
        contentDescription = contentDescription,
        modifier = if (size != null) {
            modifier.size(size).clip(shape)
        } else {
            modifier.clip(shape)
        },
        contentScale = contentScale
    )
}

/**
 * Versão otimizada para imagens de receitas com cache específico
 */
@Composable
fun RecipeCachedImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    CachedImage(
        url = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        showPlaceholder = true,
        showError = true
    )
}

/**
 * Versão para avatares de usuário com cache otimizado
 */
@Composable
fun AvatarCachedImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    CachedImage(
        url = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        shape = androidx.compose.foundation.shape.CircleShape,
        size = size,
        showPlaceholder = true,
        showError = true
    )
} 