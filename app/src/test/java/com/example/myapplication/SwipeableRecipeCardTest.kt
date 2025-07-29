package com.example.myapplication

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.ui.components.SwipeableRecipeCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SwipeableRecipeCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSwipeToFavorite() {
        val receita = ReceitaEntity(
            id = "test_recipe_1",
            nome = "Receita de Teste",
            descricaoCurta = "Uma receita para teste",
            imagemUrl = "https://example.com/image.jpg",
            ingredientes = listOf("ingrediente 1", "ingrediente 2"),
            modoPreparo = listOf("passo 1", "passo 2"),
            tempoPreparo = "30 minutos",
            porcoes = 4,
            userId = "user_1",
            userEmail = "test@example.com",
            curtidas = emptyList(),
            favoritos = emptyList(),
            tags = listOf("teste", "rápido")
        )

        var favoriteActionCalled = false
        var cardClickActionCalled = false

        composeTestRule.setContent {
            SwipeableRecipeCard(
                receita = receita,
                onSwipeToFavorite = {
                    favoriteActionCalled = true
                },
                onCardClick = {
                    cardClickActionCalled = true
                },
                onFavoriteClick = { isFavorite ->
                    // Ação do botão de favorito
                }
            )
        }

        // Verificar se o card é exibido
        composeTestRule.onNodeWithText("Receita de Teste").assertExists()

        // Fazer swipe para direita para favoritar
        composeTestRule.onNodeWithText("Receita de Teste").performTouchInput {
            swipeRight()
        }

        // Verificar se a ação de favoritar foi chamada
        assert(favoriteActionCalled)
    }

    @Test
    fun testBidirectionalSwipe() {
        val receita = ReceitaEntity(
            id = "test_recipe_2",
            nome = "Receita Bidirecional",
            descricaoCurta = "Teste de swipe bidirecional",
            imagemUrl = "https://example.com/image2.jpg",
            ingredientes = listOf("ingrediente 1"),
            modoPreparo = listOf("passo 1"),
            tempoPreparo = "15 minutos",
            porcoes = 2,
            userId = "user_1",
            userEmail = "test@example.com",
            curtidas = emptyList(),
            favoritos = listOf("user_1"), // Já favoritada
            tags = listOf("teste")
        )

        var favoriteActionCalled = false
        var unfavoriteActionCalled = false

        composeTestRule.setContent {
            com.example.myapplication.ui.components.BidirectionalSwipeableRecipeCard(
                receita = receita,
                onSwipeToFavorite = {
                    favoriteActionCalled = true
                },
                onSwipeToUnfavorite = {
                    unfavoriteActionCalled = true
                },
                onCardClick = {
                    // Ação de clique
                },
                onFavoriteClick = { isFavorite ->
                    // Ação do botão
                }
            )
        }

        // Verificar se o card é exibido
        composeTestRule.onNodeWithText("Receita Bidirecional").assertExists()

        // Fazer swipe para esquerda para desfavoritar
        composeTestRule.onNodeWithText("Receita Bidirecional").performTouchInput {
            swipeLeft()
        }

        // Verificar se a ação de desfavoritar foi chamada
        assert(unfavoriteActionCalled)
    }

    @Test
    fun testEnhancedSwipeWithAnimation() {
        val receita = ReceitaEntity(
            id = "test_recipe_3",
            nome = "Receita Aprimorada",
            descricaoCurta = "Teste de swipe aprimorado",
            imagemUrl = "https://example.com/image3.jpg",
            ingredientes = listOf("ingrediente 1", "ingrediente 2", "ingrediente 3"),
            modoPreparo = listOf("passo 1", "passo 2", "passo 3"),
            tempoPreparo = "45 minutos",
            porcoes = 6,
            userId = "user_1",
            userEmail = "test@example.com",
            curtidas = emptyList(),
            favoritos = emptyList(),
            tags = listOf("teste", "elaborado")
        )

        var favoriteActionCalled = false

        composeTestRule.setContent {
            com.example.myapplication.ui.components.EnhancedSwipeableRecipeCard(
                receita = receita,
                onSwipeToFavorite = {
                    favoriteActionCalled = true
                },
                onCardClick = {
                    // Ação de clique
                },
                onFavoriteClick = { isFavorite ->
                    // Ação do botão
                }
            )
        }

        // Verificar se o card é exibido
        composeTestRule.onNodeWithText("Receita Aprimorada").assertExists()

        // Fazer swipe para direita para favoritar
        composeTestRule.onNodeWithText("Receita Aprimorada").performTouchInput {
            swipeRight()
        }

        // Verificar se a ação de favoritar foi chamada
        assert(favoriteActionCalled)
    }
} 