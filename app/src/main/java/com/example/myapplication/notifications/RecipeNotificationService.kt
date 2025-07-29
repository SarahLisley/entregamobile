package com.example.myapplication.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.myapplication.R
import com.example.myapplication.core.data.database.AppDatabase
import com.example.myapplication.core.data.database.dao.ReceitaDao
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.workers.RecipeReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Serviço para gerenciar notificações inteligentes de receitas
 */
class RecipeNotificationService(private val context: Context) {

    private val receitaDao: ReceitaDao by lazy {
        AppDatabase.getDatabase(context).receitaDao()
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val CHANNEL_ID_FAVORITES = "recipe_favorites"
        const val CHANNEL_ID_INGREDIENTS = "recipe_ingredients"
        const val CHANNEL_ID_MEAL_TIME = "recipe_meal_time"
        const val CHANNEL_ID_GENERAL = "recipe_general"
    }

    init {
        createNotificationChannels()
    }

    /**
     * Cria os canais de notificação
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_FAVORITES,
                    "Receitas Favoritas",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Lembretes para suas receitas favoritas"
                },
                NotificationChannel(
                    CHANNEL_ID_INGREDIENTS,
                    "Ingredientes Disponíveis",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Receitas que você pode fazer com ingredientes disponíveis"
                },
                NotificationChannel(
                    CHANNEL_ID_MEAL_TIME,
                    "Horário das Refeições",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Sugestões de receitas para o horário atual"
                },
                NotificationChannel(
                    CHANNEL_ID_GENERAL,
                    "Lembretes Gerais",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Outros lembretes relacionados a receitas"
                }
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    /**
     * Agenda um lembrete para uma receita específica
     */
    fun scheduleRecipeReminder(
        recipeId: String,
        notificationType: String = RecipeReminderWorker.TYPE_FAVORITE_REMINDER,
        delayMinutes: Long = 60
    ) {
        RecipeReminderWorker.scheduleRecipeReminder(
            context = context,
            recipeId = recipeId,
            notificationType = notificationType,
            delayMinutes = delayMinutes
        )
    }

    /**
     * Agenda lembretes para receitas favoritas
     */
    fun scheduleFavoriteReminders() {
        scope.launch {
            receitaDao.getAllReceitas().collect { allRecipes ->
                val favoriteRecipes = allRecipes.filter { recipe -> recipe.favoritos.isNotEmpty() }
                
                favoriteRecipes.forEach { recipe ->
                    // Agenda um lembrete para cada receita favorita
                    RecipeReminderWorker.scheduleRecipeReminder(
                        context = context,
                        recipeId = recipe.id,
                        notificationType = RecipeReminderWorker.TYPE_FAVORITE_REMINDER,
                        delayMinutes = 24 * 60 // 24 horas
                    )
                }
            }
        }
    }

    /**
     * Agenda lembretes baseados em ingredientes disponíveis
     */
    fun scheduleIngredientBasedReminders() {
        scope.launch {
            receitaDao.getAllReceitas().collect { allRecipes ->
                val availableIngredients = getAvailableIngredients()
                
                allRecipes.forEach { recipe ->
                    val matchingIngredients = recipe.ingredientes.count { ingredient ->
                        availableIngredients.any { available ->
                            available.contains(ingredient, ignoreCase = true) ||
                            ingredient.contains(available, ignoreCase = true)
                        }
                    }
                    
                    // Se pelo menos 70% dos ingredientes estão disponíveis
                    if (matchingIngredients >= (recipe.ingredientes.size * 0.7)) {
                        RecipeReminderWorker.scheduleRecipeReminder(
                            context = context,
                            recipeId = recipe.id,
                            notificationType = RecipeReminderWorker.TYPE_INGREDIENT_REMINDER,
                            delayMinutes = 2 * 60 // 2 horas
                        )
                    }
                }
            }
        }
    }

    /**
     * Agenda lembretes baseados no horário das refeições
     */
    fun scheduleMealTimeReminders() {
        scope.launch {
            receitaDao.getAllReceitas().collect { allRecipes ->
                val currentHour = java.time.LocalTime.now().hour
                
                allRecipes.forEach { recipe ->
                    val shouldSchedule = when {
                        currentHour in 7..9 -> isBreakfastRecipe(recipe)
                        currentHour in 11..13 -> isLunchRecipe(recipe)
                        currentHour in 18..20 -> isDinnerRecipe(recipe)
                        else -> false
                    }
                    
                    if (shouldSchedule) {
                        RecipeReminderWorker.scheduleRecipeReminder(
                            context = context,
                            recipeId = recipe.id,
                            notificationType = RecipeReminderWorker.TYPE_MEAL_TIME_REMINDER,
                            delayMinutes = 30 // 30 minutos
                        )
                    }
                }
            }
        }
    }

    /**
     * Mostra uma notificação imediata
     */
    fun showImmediateNotification(
        recipe: ReceitaEntity,
        notificationType: String = RecipeReminderWorker.TYPE_FAVORITE_REMINDER
    ) {
        val (title, content, channelId) = when (notificationType) {
            RecipeReminderWorker.TYPE_FAVORITE_REMINDER -> {
                Triple(
                    "Que tal preparar ${recipe.nome}?",
                    "Sua receita favorita está esperando por você!",
                    CHANNEL_ID_FAVORITES
                )
            }
            RecipeReminderWorker.TYPE_INGREDIENT_REMINDER -> {
                Triple(
                    "Ingredientes disponíveis para ${recipe.nome}",
                    "Você tem os ingredientes necessários para preparar esta receita!",
                    CHANNEL_ID_INGREDIENTS
                )
            }
            RecipeReminderWorker.TYPE_MEAL_TIME_REMINDER -> {
                Triple(
                    "Hora de cozinhar: ${recipe.nome}",
                    "Perfeita para este horário!",
                    CHANNEL_ID_MEAL_TIME
                )
            }
            else -> {
                Triple(
                    "Lembrete de receita",
                    "Que tal preparar ${recipe.nome}?",
                    CHANNEL_ID_GENERAL
                )
            }
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_reminder)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(recipe.id.hashCode(), builder.build())
    }

    /**
     * Agenda lembretes periódicos
     */
    fun schedulePeriodicReminders() {
        // Lembretes diários para receitas favoritas
        RecipeReminderWorker.schedulePeriodicFavoriteReminders(context)
        
        // Lembretes baseados em ingredientes (a cada 6 horas)
        schedulePeriodicIngredientReminders()
        
        // Lembretes baseados no horário das refeições (a cada 4 horas)
        schedulePeriodicMealTimeReminders()
    }

    /**
     * Agenda lembretes periódicos baseados em ingredientes
     */
    private fun schedulePeriodicIngredientReminders() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<RecipeReminderWorker>(
            6, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "periodic_ingredient_reminders",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork
            )
    }

    /**
     * Agenda lembretes periódicos baseados no horário das refeições
     */
    private fun schedulePeriodicMealTimeReminders() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<RecipeReminderWorker>(
            4, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "periodic_meal_time_reminders",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork
            )
    }

    /**
     * Cancela todos os lembretes
     */
    fun cancelAllReminders() {
        WorkManager.getInstance(context).cancelAllWork()
    }

    /**
     * Cancela lembretes para uma receita específica
     */
    fun cancelRecipeReminders(recipeId: String) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("recipe_reminder_worker_$recipeId")
    }

    /**
     * Verifica se uma receita é de café da manhã
     */
    private fun isBreakfastRecipe(recipe: ReceitaEntity): Boolean {
        val recipeName = recipe.nome.lowercase()
        val tags = recipe.tags.map { it.lowercase() }
        
        return recipeName.contains("café") || recipeName.contains("pão") ||
                recipeName.contains("ovos") || recipeName.contains("leite") ||
                tags.any { it.contains("café") || it.contains("café da manhã") }
    }

    /**
     * Verifica se uma receita é de almoço
     */
    private fun isLunchRecipe(recipe: ReceitaEntity): Boolean {
        val recipeName = recipe.nome.lowercase()
        val tags = recipe.tags.map { it.lowercase() }
        
        return recipeName.contains("almoço") || recipeName.contains("prato") ||
                recipeName.contains("arroz") || recipeName.contains("feijão") ||
                tags.any { it.contains("almoço") || it.contains("prato principal") }
    }

    /**
     * Verifica se uma receita é de jantar
     */
    private fun isDinnerRecipe(recipe: ReceitaEntity): Boolean {
        val recipeName = recipe.nome.lowercase()
        val tags = recipe.tags.map { it.lowercase() }
        
        return recipeName.contains("jantar") || recipeName.contains("ceia") ||
                recipeName.contains("sopa") || recipeName.contains("salada") ||
                tags.any { it.contains("jantar") || it.contains("ceia") }
    }

    /**
     * Simula obtenção de ingredientes disponíveis
     */
    private fun getAvailableIngredients(): List<String> {
        // Em uma implementação real, isso viria do banco de dados do usuário
        return listOf(
            "ovos", "leite", "farinha", "açúcar", "sal", "azeite",
            "cebola", "alho", "tomate", "frango", "carne", "arroz",
            "feijão", "batata", "cenoura", "abobrinha", "queijo"
        )
    }
} 