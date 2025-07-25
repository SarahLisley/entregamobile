# Configuração da API Nutricional (Requisito F13)

## Visão Geral
O app agora inclui integração com a API Spoonacular para buscar informações nutricionais das receitas. Esta funcionalidade atende ao requisito F13 (Retrofit).

## Configuração

### 1. Obter Chave da API
1. Acesse [Spoonacular](https://spoonacular.com/food-api)
2. Crie uma conta gratuita
3. Obtenha sua chave de API

### 2. Configurar a Chave
1. Abra o arquivo `app/src/main/java/com/example/myapplication/data/NutritionApiService.kt`
2. Substitua `YOUR_SPOONACULAR_API_KEY` pela sua chave real:

```kotlin
@Query("apiKey") apiKey: String = "sua_chave_aqui"
```

### 3. Alternativa: Configuração via BuildConfig
Para maior segurança, você pode configurar a chave via `local.properties`:

1. Adicione ao arquivo `local.properties`:
```
SPOONACULAR_API_KEY=sua_chave_aqui
```

2. Adicione ao `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "SPOONACULAR_API_KEY", "\"${localProperties.getProperty("SPOONACULAR_API_KEY") ?: ""}\"")
```

3. Atualize o `NutritionApiService.kt`:
```kotlin
@Query("apiKey") apiKey: String = BuildConfig.SPOONACULAR_API_KEY
```

## Funcionalidades Implementadas

### 1. Busca de Informações Nutricionais
- Botão "Info" na tela de detalhes da receita
- Busca automática baseada no nome da receita
- Exibição de calorias, proteínas, gorduras, carboidratos, fibras e açúcares

### 2. Modelos de Dados
- `NutritionResponse`: Resposta da API
- `RecipeNutrition`: Modelo simplificado para uso no app
- `NutritionRepository`: Gerenciamento das chamadas da API

### 3. Integração com ViewModel
- Estado reativo para informações nutricionais
- Tratamento de erros
- Feedback ao usuário via Snackbars

## Limitações da API Gratuita
- 150 requisições por dia
- Dados nutricionais estimados (não precisos)
- Algumas receitas podem não ser encontradas

## Próximos Passos
1. Implementar cache local das informações nutricionais
2. Adicionar mais detalhes nutricionais
3. Implementar busca por ingredientes específicos
4. Adicionar comparação nutricional entre receitas

## Arquivos Modificados
- `NutritionApiService.kt`: Interface Retrofit
- `NutritionRepository.kt`: Repositório da API
- `NutritionData.kt`: Modelos de dados
- `ReceitasViewModel.kt`: Integração com ViewModel
- `DetalheScreen.kt`: Interface do usuário
- `build.gradle.kts`: Dependências do Retrofit
- `libs.versions.toml`: Versões das bibliotecas 