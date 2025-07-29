# Sistema de Favoritos com Swipe e Notificações Inteligentes

## Visão Geral

Este documento descreve a implementação do sistema de favoritos com swipe e notificações inteligentes no aplicativo NutriLivre.

## 🎯 Funcionalidades Implementadas

### 1. Sistema de Favoritos com Swipe

#### Componentes Disponíveis

- **SwipeableRecipeCard**: Card básico com swipe para favoritar
- **BidirectionalSwipeableRecipeCard**: Card com swipe bidirecional (favoritar/desfavoritar)
- **EnhancedSwipeableRecipeCard**: Card com feedback visual aprimorado

#### Como Usar

```kotlin
// Swipe básico para favoritar
SwipeableRecipeCard(
    receita = receita,
    onSwipeToFavorite = {
        // Ação quando o usuário faz swipe para favoritar
        receitasViewModel.favoritarReceita(receita.id, userId, favoritos)
    },
    onCardClick = { 
        // Navegação para detalhes da receita
        navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
    },
    onFavoriteClick = { isFavorite ->
        // Ação quando o botão de favorito é clicado
        receitasViewModel.favoritarReceita(receita.id, userId, favoritos)
    }
)

// Swipe bidirecional
BidirectionalSwipeableRecipeCard(
    receita = receita,
    onSwipeToFavorite = {
        // Ação quando faz swipe para direita (favoritar)
    },
    onSwipeToUnfavorite = {
        // Ação quando faz swipe para esquerda (desfavoritar)
    },
    onCardClick = { /* navegação */ },
    onFavoriteClick = { /* ação do botão */ }
)
```

#### Implementação nas Telas

1. **Tela de Favoritos**: Usa `BidirectionalSwipeableRecipeCard` para permitir desfavoritar
2. **Tela Principal**: Usa `SwipeableRecipeCard` para favoritar receitas
3. **Tela de Busca**: Usa `SwipeableRecipeCard` para favoritar resultados

### 2. Notificações Inteligentes

#### Tipos de Notificações

1. **Lembretes para Favoritos**: Notifica sobre receitas favoritas não vistas há 7 dias
2. **Lembretes por Ingredientes**: Sugere receitas baseadas em ingredientes disponíveis
3. **Lembretes por Horário**: Sugere receitas apropriadas para o horário atual

#### Configuração

```kotlin
// Inicializar serviço de notificações
val notificationService = RecipeNotificationService(context)

// Agendar lembretes para favoritos
notificationService.scheduleFavoriteReminders()

// Agendar lembretes baseados em ingredientes
notificationService.scheduleIngredientBasedReminders()

// Agendar lembretes por horário
notificationService.scheduleMealTimeReminders()

// Cancelar todos os lembretes
notificationService.cancelAllReminders()
```

#### Worker de Notificações

O `RecipeReminderWorker` é responsável por:

- Verificar se deve mostrar notificações baseado em critérios inteligentes
- Mostrar notificações com títulos e conteúdos personalizados
- Gerenciar diferentes canais de notificação

```kotlin
// Agendar lembrete específico
RecipeReminderWorker.scheduleRecipeReminder(
    context = context,
    recipeId = "recipe_id",
    notificationType = RecipeReminderWorker.TYPE_FAVORITE_REMINDER,
    delayMinutes = 60
)
```

## 🎨 Características Visuais

### Animações de Swipe

- **Escala do ícone**: O ícone de favorito aumenta de tamanho durante o swipe
- **Cor de fundo**: Muda para a cor primária do tema durante o swipe
- **Feedback visual**: Diferentes cores para favoritar (primária) e desfavoritar (erro)

### Notificações

- **Canais separados**: Diferentes canais para tipos de notificação
- **Conteúdo personalizado**: Títulos e mensagens específicas para cada tipo
- **Ícones temáticos**: Ícones apropriados para cada tipo de lembrete

## 🔧 Configuração

### 1. Permissões Necessárias

Adicione ao `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

### 2. Inicialização na MainActivity

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Configurar sistema de notificações
    configurarNotificacoes()
}

private fun configurarNotificacoes() {
    val notificationService = RecipeNotificationService(this)
    notificationService.schedulePeriodicReminders()
}
```

### 3. Configurações do Usuário

Na tela de configurações, o usuário pode:

- Agendar lembretes para receitas favoritas
- Configurar lembretes baseados em ingredientes
- Definir lembretes por horário das refeições
- Cancelar todos os lembretes

## 📱 Uso nas Telas

### Tela de Favoritos

```kotlin
FavoritesListView(
    favoritos = filteredAndSortedFavorites,
    onRecipeClick = { receita ->
        navController.navigate(AppScreens.DetalheScreen.createRoute(receita.id))
    },
    onFavoriteClick = { id, userId, favoritos ->
        receitasViewModel.favoritarReceita(id, userId, favoritos)
    }
)
```

### Lista Paginada

```kotlin
ReceitasPaginatedList(
    receitasPagingData = receitasPagingData,
    onReceitaClick = { receita ->
        // Navegação
    },
    onReceitaFavorite = { receita, isFavorite ->
        // Ação de favoritar
    }
)
```

## 🎯 Critérios de Notificação

### Lembretes para Favoritos

- Receita está nos favoritos do usuário
- Não foi visualizada nos últimos 7 dias
- Usuário tem histórico de interação com receitas

### Lembretes por Ingredientes

- Pelo menos 70% dos ingredientes estão disponíveis
- Receita não foi preparada recentemente
- Baseado em lista de ingredientes do usuário

### Lembretes por Horário

- **Café da manhã**: 7h-9h
- **Almoço**: 11h-13h  
- **Jantar**: 18h-20h
- Receita apropriada para o horário

## 🔄 Ciclo de Vida

### Swipe

1. Usuário inicia swipe
2. Animação visual é exibida
3. Se swipe atinge o limite, ação é executada
4. Card retorna à posição original

### Notificações

1. Worker verifica critérios periodicamente
2. Se critérios são atendidos, notificação é criada
3. Notificação é exibida com conteúdo personalizado
4. Usuário pode interagir com a notificação

## 🚀 Próximos Passos

1. **Personalização**: Permitir que usuários configurem critérios
2. **Machine Learning**: Usar ML para melhorar sugestões
3. **Integração**: Conectar com lista de compras
4. **Analytics**: Rastrear eficácia das notificações

## 📝 Notas de Implementação

- Sistema funciona offline
- Notificações são agendadas localmente
- Swipe é responsivo e acessível
- Código é modular e reutilizável
- Testes unitários recomendados para workers 