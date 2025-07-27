package com.example.myapplication.data

import com.example.myapplication.BuildConfig
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.model.ChatMessage
import com.example.myapplication.core.data.network.ChatService
import com.example.myapplication.core.data.network.NutritionService
import com.example.myapplication.core.data.network.ImageGenerationService
import com.example.myapplication.core.data.SupabaseImageUploader
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

object GeminiService : NutritionService, ChatService {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val gson = Gson()
    private val imageGenerationService = ImageGenerationService()

    override suspend fun getNutritionalInfo(recipe: ReceitaEntity): RecipeNutrition {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Analise a seguinte receita e forneça uma análise nutricional estimada por porção.
                    Nome da Receita: ${recipe.nome}
                    Porções: ${recipe.porcoes}
                    Ingredientes: ${recipe.ingredientes.joinToString(", ")}

                    Retorne a análise como um objeto JSON com os valores estimados para uma única porção.
                    As chaves devem ser: "calories", "protein", "fat", "carbohydrates", "fiber", "sugar".
                    Se a informação de fibra ou açúcar não for facilmente determinável, use null.
                    O JSON deve ser o único conteúdo na sua resposta.
                    
                    Exemplo de formato:
                    {
                        "calories": 250.0,
                        "protein": 10.0,
                        "fat": 12.0,
                        "carbohydrates": 25.0,
                        "fiber": 2.0,
                        "sugar": 8.0
                    }
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: throw Exception("Resposta vazia do Gemini")
                
                // Extrair JSON da resposta
                val jsonMatch = Regex("\\{[^}]*\"calories\"[^}]*\\}").find(responseText)
                val jsonString = jsonMatch?.value ?: throw Exception("JSON não encontrado na resposta")
                
                gson.fromJson(jsonString, RecipeNutrition::class.java)
            } catch (e: Exception) {
                // Fallback para valores padrão em caso de erro
                RecipeNutrition(250.0, 10.0, 12.0, 25.0, 2.0, 8.0)
            }
        }
    }

    override suspend fun continueChat(history: List<ChatMessage>, newMessage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val conversationHistory = history.joinToString("\n") { 
                    "${if (it.isUser) "Usuário" else "Chef Gemini"}: ${it.content}" 
                }
                
                val prompt = """
                    Você é o Chef Gemini, um chef especializado em nutrição e culinária. 
                    Seja entusiasta, criativo e prestativo. Seu objetivo é inspirar os usuários 
                    e ajudá-los a criar pratos incríveis. Quando um usuário pedir para gerar 
                    uma receita, confirme que você pode criar uma receita maravilhosa baseada na conversa.
                    
                    Histórico da conversa:
                    $conversationHistory
                    
                    Usuário: $newMessage
                    
                    Chef Gemini:
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                response.text ?: "Desculpe, não consegui processar sua mensagem."
            } catch (e: Exception) {
                "Desculpe, ocorreu um erro ao processar sua mensagem. Tente novamente."
            }
        }
    }

    override suspend fun generateRecipeFromConversation(history: List<ChatMessage>): ReceitaEntity {
        return withContext(Dispatchers.IO) {
            try {
                val conversationHistory = history.joinToString("\n") { 
                    "${if (it.isUser) "Usuário" else "Chef Gemini"}: ${it.content}" 
                }
                
                val prompt = """
                    Com base na conversa abaixo, gere uma receita completa em formato JSON.
                    
                    Histórico da conversa:
                    $conversationHistory
                    
                    Gere uma receita criativa e deliciosa que atenda às preferências e necessidades 
                    mencionadas na conversa. A receita deve ser completa e detalhada.
                    
                    Retorne apenas o JSON da receita, sem texto adicional.
                    
                    Formato do JSON:
                    {
                        "nome": "Nome da Receita",
                        "descricaoCurta": "Descrição breve e atrativa da receita",
                        "ingredientes": ["ingrediente 1", "ingrediente 2", ...],
                        "modoPreparo": ["passo 1", "passo 2", ...],
                        "tempoPreparo": "30 minutos",
                        "porcoes": 4,
                        "tags": ["tag1", "tag2"]
                    }
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: throw Exception("Resposta vazia do Gemini")
                
                // Extrair JSON da resposta
                val jsonMatch = Regex("\\{[^}]*\"nome\"[^}]*\\}").find(responseText)
                val jsonString = jsonMatch?.value ?: throw Exception("JSON não encontrado na resposta")
                
                val recipeData = gson.fromJson(jsonString, RecipeData::class.java)
                
                // Criar receita sem imagem inicialmente
                ReceitaEntity(
                    id = System.currentTimeMillis().toString(),
                    nome = recipeData.nome,
                    descricaoCurta = recipeData.descricaoCurta,
                    imagemUrl = "", // Será preenchida pelo generateImageForRecipe
                    ingredientes = recipeData.ingredientes,
                    modoPreparo = recipeData.modoPreparo,
                    tempoPreparo = recipeData.tempoPreparo,
                    porcoes = recipeData.porcoes,
                    userId = "gemini_generated",
                    userEmail = "chef@gemini.com",
                    curtidas = emptyList(),
                    favoritos = emptyList(),
                    tags = recipeData.tags,
                    isSynced = false
                )
            } catch (e: Exception) {
                // Receita padrão em caso de erro
                ReceitaEntity(
                    id = System.currentTimeMillis().toString(),
                    nome = "Receita Especial",
                    descricaoCurta = "Uma receita deliciosa gerada pelo Chef Gemini",
                    imagemUrl = "",
                    ingredientes = listOf("Ingrediente 1", "Ingrediente 2", "Ingrediente 3"),
                    modoPreparo = listOf("Passo 1", "Passo 2", "Passo 3"),
                    tempoPreparo = "30 minutos",
                    porcoes = 4,
                    userId = "gemini_generated",
                    userEmail = "chef@gemini.com",
                    curtidas = emptyList(),
                    favoritos = emptyList(),
                    tags = listOf("especial", "gerado"),
                    isSynced = false
                )
            }
        }
    }

    /**
     * Gera uma imagem para uma receita usando a nova API do Cloudflare Worker
     * Implementação baseada no plan.md com Stable Diffusion XL + Gemini Flash
     * @param recipe Receita para gerar a imagem
     * @return Receita com a imagem gerada
     */
    override suspend fun generateImageForRecipe(recipe: ReceitaEntity): ReceitaEntity {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("GeminiService", "=== INICIANDO GERAÇÃO DE IMAGEM PARA RECEITA ===")
                Log.d("GeminiService", "Receita: ${recipe.nome}")
                
                // Gerar imagem usando o ImageGenerationService
                val imageUrl = imageGenerationService.generateRecipeImage(recipe.nome)
                
                // Retornar receita com a imagem gerada
                recipe.copy(imagemUrl = imageUrl)
                
            } catch (e: Exception) {
                Log.e("GeminiService", "Erro ao gerar imagem para receita: ${e.message}")
                // Retornar receita com imagem fallback
                recipe.copy(imagemUrl = getFallbackImageUrl(recipe.nome))
            }
        }
    }

    /**
     * Gera uma URL de fallback para imagens
     * @param recipeName Nome da receita
     * @return URL de fallback
     */
    private fun getFallbackImageUrl(recipeName: String): String {
        val sanitizedName = recipeName.replace(" ", "").replace("[^a-zA-Z0-9]".toRegex(), "")
        // Usar Unsplash para imagens de comida de alta qualidade
        return "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=300&fit=crop"
    }

    private data class RecipeData(
        val nome: String,
        val descricaoCurta: String,
        val ingredientes: List<String>,
        val modoPreparo: List<String>,
        val tempoPreparo: String,
        val porcoes: Int,
        val tags: List<String> = emptyList()
    )
} 