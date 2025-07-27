package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.core.data.network.ImageGenerationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Classe de teste para verificar a nova funcionalidade do ImageGenerationService
 * Testa a geração de imagens usando o Worker modificado que retorna URLs públicas
 */
class TestImageGeneration {
    
    companion object {
        private const val TAG = "TestImageGeneration"
        
        /**
         * Testa a geração de imagem para uma receita
         * @param recipeName Nome da receita para testar
         */
        fun testImageGeneration(recipeName: String) {
            val imageService = ImageGenerationService()
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(TAG, "🧪 INICIANDO TESTE DE GERAÇÃO DE IMAGEM")
                    Log.d(TAG, "Receita de teste: $recipeName")
                    
                    val imageUrl = imageService.generateRecipeImage(recipeName)
                    
                    Log.d(TAG, "✅ TESTE CONCLUÍDO!")
                    Log.d(TAG, "URL da imagem gerada: $imageUrl")
                    
                    // Verificar se é uma URL válida
                    if (imageUrl.startsWith("http")) {
                        Log.d(TAG, "✅ URL válida detectada - Compatível com Coil")
                    } else if (imageUrl.startsWith("data:")) {
                        Log.w(TAG, "⚠️ Data URL detectada - Pode ter problemas com Coil")
                    } else {
                        Log.e(TAG, "❌ URL inválida detectada")
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Erro no teste: ${e.message}")
                }
            }
        }
        
        /**
         * Testa múltiplas receitas para verificar a consistência
         */
        fun testMultipleRecipes() {
            val recipes = listOf(
                "Bolo de Chocolate",
                "Pizza Margherita",
                "Salada Caesar",
                "Sopa de Tomate",
                "Torta de Maçã"
            )
            
            recipes.forEach { recipe ->
                testImageGeneration(recipe)
                // Aguardar um pouco entre os testes
                Thread.sleep(2000)
            }
        }
    }
} 