package com.example.myapplication.core.data.network

import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.model.RecipeNutrition
import com.example.myapplication.core.data.model.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class GeminiServiceImpl(private val apiKey: String) : NutritionService, ChatService {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash-lite",
        apiKey = apiKey
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
                
                // Extrair JSON da resposta com parsing mais robusto
                val jsonString = extractJsonFromResponse(responseText, "calories")
                    ?: throw Exception("JSON não encontrado na resposta")
                
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
                    Você é o Chef Gemini, um chef especializado em nutrição e culinária brasileira e internacional.
                    
                    PERSONALIDADE E ESTILO:
                    - Seja entusiasta, criativo, prestativo e acolhedor
                    - Use linguagem acessível mas técnica quando necessário
                    - Demonstre conhecimento profundo em culinária, nutrição e técnicas culinárias
                    - Seja paciente e didático, explicando conceitos quando relevante
                    - Use emojis ocasionalmente para tornar a conversa mais amigável
                    
                    CAPACIDADES:
                    - Criar receitas personalizadas baseadas em preferências, restrições e ingredientes disponíveis
                    - Fornecer dicas de culinária, técnicas e truques
                    - Explicar informações nutricionais e benefícios dos ingredientes
                    - Sugerir substituições e adaptações de receitas
                    - Recomendar combinações de sabores e harmonização
                    - Ajudar com planejamento de refeições e organização da cozinha
                    
                    DIRETRIZES DE RESPOSTA:
                    - Sempre seja útil e construtivo
                    - Se o usuário pedir uma receita, confirme que pode criar uma receita maravilhosa baseada na conversa
                    - Quando apropriado, faça perguntas para entender melhor as preferências do usuário
                    - Sugira variações e opções quando relevante
                    - Se não souber algo, seja honesto e sugira alternativas
                    - Mantenha o foco em culinária, nutrição e gastronomia
                    
                    CONTEXTO DA CONVERSA:
                    $conversationHistory
                    
                    USUÁRIO: $newMessage
                    
                    CHEF GEMINI:
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                response.text ?: "Desculpe, não consegui processar sua mensagem. Tente novamente."
            } catch (e: Exception) {
                "Desculpe, ocorreu um erro ao processar sua mensagem. Tente novamente."
            }
        }
    }

    override suspend fun generateRecipeFromConversation(
        history: List<ChatMessage>,
        userId: String?,
        userEmail: String?
    ): ReceitaEntity {
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
                        "ingredientes": ["ingrediente 1", "ingrediente 2"],
                        "modoPreparo": ["passo 1", "passo 2"],
                        "tempoPreparo": "30 minutos",
                        "porcoes": 4,
                        "tags": ["tag1", "tag2"]
                    }
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: throw Exception("Resposta vazia do Gemini")
                
                // Extrair JSON da resposta com parsing mais robusto
                val jsonString = extractJsonFromResponse(responseText, "nome")
                    ?: throw Exception("JSON não encontrado na resposta")
                
                val recipeData = gson.fromJson(jsonString, RecipeData::class.java)
                
                // Usar informações do usuário logado se fornecidas, senão usar valores padrão
                val finalUserId = userId ?: "gemini_generated"
                val finalUserEmail = userEmail ?: "chef@gemini.com"
                
                // Criar receita sem imagem inicialmente
                ReceitaEntity(
                    id = "recipe_${System.currentTimeMillis()}",
                    nome = recipeData.nome,
                    descricaoCurta = recipeData.descricaoCurta,
                    imagemUrl = "", // Será preenchida pelo generateImageForRecipe
                    ingredientes = recipeData.ingredientes,
                    modoPreparo = recipeData.modoPreparo,
                    tempoPreparo = recipeData.tempoPreparo,
                    porcoes = recipeData.porcoes,
                    userId = finalUserId,
                    userEmail = finalUserEmail,
                    curtidas = emptyList(),
                    favoritos = emptyList(),
                    tags = recipeData.tags,
                    isSynced = false
                )
            } catch (e: Exception) {
                // Receita padrão em caso de erro
                val finalUserId = userId ?: "gemini_generated"
                val finalUserEmail = userEmail ?: "chef@gemini.com"
                
                ReceitaEntity(
                    id = "recipe_${System.currentTimeMillis()}",
                    nome = "Receita Especial",
                    descricaoCurta = "Uma receita deliciosa gerada pelo Chef Gemini",
                    imagemUrl = "",
                    ingredientes = emptyList(),
                    modoPreparo = emptyList(),
                    tempoPreparo = "30 minutos",
                    porcoes = 4,
                    userId = finalUserId,
                    userEmail = finalUserEmail,
                    curtidas = emptyList(),
                    favoritos = emptyList(),
                    tags = listOf("especial", "gerado"),
                    isSynced = false
                )
            }
        }
    }

    override suspend fun generateImageForRecipe(recipe: ReceitaEntity): ReceitaEntity {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("GeminiServiceImpl", "=== INICIANDO GERAÇÃO DE IMAGEM PARA RECEITA ===")
                Log.d("GeminiServiceImpl", "Receita: ${recipe.nome}")
                Log.d("GeminiServiceImpl", "ID da receita: ${recipe.id}")
                
                // Gerar imagem usando o ImageGenerationService
                Log.d("GeminiServiceImpl", "🔄 Chamando ImageGenerationService...")
                val imageUrl = imageGenerationService.generateRecipeImage(recipe.nome)
                Log.d("GeminiServiceImpl", "✅ Imagem gerada com sucesso!")
                Log.d("GeminiServiceImpl", "🖼️ URL da imagem: $imageUrl")
                
                // Retornar receita com a imagem gerada
                val recipeWithImage = recipe.copy(imagemUrl = imageUrl)
                Log.d("GeminiServiceImpl", "📝 Receita atualizada com imagem")
                return@withContext recipeWithImage
                
            } catch (e: Exception) {
                Log.e("GeminiServiceImpl", "💥 ERRO na geração de imagem para receita")
                Log.e("GeminiServiceImpl", "💥 Tipo da exceção: ${e.javaClass.simpleName}")
                Log.e("GeminiServiceImpl", "💥 Mensagem: ${e.message}")
                Log.e("GeminiServiceImpl", "💥 Stack trace:")
                e.printStackTrace()
                
                // Retornar receita com imagem fallback
                val fallbackUrl = getFallbackImageUrl(recipe.nome)
                Log.d("GeminiServiceImpl", "⚠️ Usando imagem de fallback: $fallbackUrl")
                return@withContext recipe.copy(imagemUrl = fallbackUrl)
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
        
        // Lista de fallbacks em ordem de preferência
        val fallbacks = listOf(
            // Unsplash - imagens de comida de alta qualidade
            "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=300&fit=crop",
            // Picsum com seed baseado no nome da receita
            "https://picsum.photos/seed/${sanitizedName}/400/300",
            // Imagem genérica de comida
            "https://images.unsplash.com/photo-1504674900242-87b0b7b3b8c8?w=400&h=300&fit=crop"
        )
        
        // Usar o primeiro fallback disponível
        return fallbacks.first()
    }

    /**
     * Extrai JSON de uma resposta de texto usando parsing robusto
     * @param responseText Texto da resposta
     * @param requiredKey Chave obrigatória que deve estar no JSON
     * @return JSON extraído ou null se não encontrado
     */
    private fun extractJsonFromResponse(responseText: String, requiredKey: String): String? {
        try {
            // Tentar encontrar JSON completo usando diferentes padrões
            val patterns = listOf(
                // Padrão 1: JSON simples entre chaves
                Regex("\\{[^}]*\"$requiredKey\"[^}]*\\}"),
                // Padrão 2: JSON com múltiplas linhas
                Regex("\\{[\\s\\S]*?\"$requiredKey\"[\\s\\S]*?\\}"),
                // Padrão 3: JSON com escape de caracteres
                Regex("\\{[^}]*\"$requiredKey\"[^}]*\\}", RegexOption.DOT_MATCHES_ALL)
            )
            
            for (pattern in patterns) {
                val match = pattern.find(responseText)
                if (match != null) {
                    val jsonString = match.value
                    // Validar se é um JSON válido
                    try {
                        gson.fromJson(jsonString, Map::class.java)
                        return jsonString
                    } catch (e: Exception) {
                        Log.w("GeminiServiceImpl", "JSON inválido encontrado: $jsonString")
                        continue
                    }
                }
            }
            
            // Se nenhum padrão funcionou, tentar extrair manualmente
            val startIndex = responseText.indexOf('{')
            val endIndex = responseText.lastIndexOf('}')
            
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                val jsonString = responseText.substring(startIndex, endIndex + 1)
                try {
                    gson.fromJson(jsonString, Map::class.java)
                    return jsonString
                } catch (e: Exception) {
                    Log.w("GeminiServiceImpl", "JSON manual inválido: $jsonString")
                }
            }
            
            return null
        } catch (e: Exception) {
            Log.e("GeminiServiceImpl", "Erro ao extrair JSON: ${e.message}")
            return null
        }
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