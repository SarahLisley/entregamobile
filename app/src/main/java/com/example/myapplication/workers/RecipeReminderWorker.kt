package com.example.myapplication.workers

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Worker para lembretes inteligentes de receitas
 */
class RecipeReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "recipe_reminder_worker"
        const val KEY_RECIPE_ID = "recipe_id"
        const val KEY_NOTIFICATION_TYPE = "notification_type"
        
        // Tipos de notificação
        const val TYPE_FAVORITE_REMINDER = "favorite_reminder"
        const val TYPE_INGREDIENT_REMINDER = "ingredient_reminder"
        const val TYPE_MEAL_TIME_REMINDER = "meal_time_reminder"
        
        // IDs dos canais de notificação
        const val CHANNEL_ID_RECIPE_REMINDERS = "recipe_reminders"
        const val CHANNEL_ID_FAVORITES = "favorites"
        const val CHANNEL_ID_INGREDIENTS = "ingredients"
        
        /**
         * Agenda um lembrete para uma receita específica
         */
        fun scheduleRecipeReminder(
            context: Context,
            recipeId: String,
            notificationType: String = TYPE_FAVORITE_REMINDER,
            delayMinutes: Long = 60
        ) {
            val data = Data.Builder()
                .putString(KEY_RECIPE_ID, recipeId)
                .putString(KEY_NOTIFICATION_TYPE, notificationType)
                .build()
            
            val reminderWork = OneTimeWorkRequestBuilder<RecipeReminderWorker>()
                .setInputData(data)
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "${WORK_NAME}_$recipeId",
                    ExistingWorkPolicy.REPLACE,
                    reminderWork
                )
        }

        /**
         * Agenda lembretes periódicos para receitas favoritas
         */
        fun schedulePeriodicFavoriteReminders(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
            
            val periodicWork = PeriodicWorkRequestBuilder<RecipeReminderWorker>(
                1, TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "periodic_recipe_reminders",
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWork
                )
        }
    }

    private val receitaDao: ReceitaDao by lazy {
        AppDatabase.getDatabase(context).receitaDao()
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val recipeId = inputData.getString(KEY_RECIPE_ID)
            val notificationType = inputData.getString(KEY_NOTIFICATION_TYPE) ?: TYPE_FAVORITE_REMINDER
            
            if (recipeId == null) {
                return@withContext Result.failure()
            }
            
            // Verificar se deve mostrar o lembrete
            val shouldRemind = shouldShowReminder(recipeId, notificationType)
            
            if (shouldRemind) {
                val recipe = getRecipeById(recipeId)
                if (recipe != null) {
                    showNotification(recipe, notificationType)
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    /**
     * Verifica se deve mostrar o lembrete baseado em diferentes critérios
     */
    private suspend fun shouldShowReminder(recipeId: String, notificationType: String): Boolean {
        return when (notificationType) {
            TYPE_FAVORITE_REMINDER -> shouldShowFavoriteReminder(recipeId)
            TYPE_INGREDIENT_REMINDER -> shouldShowIngredientReminder(recipeId)
            TYPE_MEAL_TIME_REMINDER -> shouldShowMealTimeReminder(recipeId)
            else -> false
        }
    }

    /**
     * Verifica se deve mostrar lembrete para receitas favoritas
     */
    private suspend fun shouldShowFavoriteReminder(recipeId: String): Boolean {
        val recipe = getRecipeById(recipeId) ?: return false
        
        // Verificar se a receita está nos favoritos
        val isFavorite = recipe.favoritos.isNotEmpty()
        
        // Verificar se não foi vista recentemente (últimos 7 dias)
        val lastViewed = getLastViewedTime(recipeId)
        val daysSinceLastViewed = (System.currentTimeMillis() - lastViewed) / (1000 * 60 * 60 * 24)
        
        return isFavorite && daysSinceLastViewed > 7
    }

    /**
     * Verifica se deve mostrar lembrete baseado em ingredientes disponíveis
     */
    private suspend fun shouldShowIngredientReminder(recipeId: String): Boolean {
        val recipe = getRecipeById(recipeId) ?: return false
        
        // Simular verificação de ingredientes disponíveis
        // Em uma implementação real, isso seria baseado em dados do usuário
        val availableIngredients = getAvailableIngredients()
        val recipeIngredients = recipe.ingredientes
        
        val matchingIngredients = recipeIngredients.count { ingredient ->
            availableIngredients.any { available ->
                available.contains(ingredient, ignoreCase = true) ||
                ingredient.contains(available, ignoreCase = true)
            }
        }
        
        // Mostrar se pelo menos 70% dos ingredientes estão disponíveis
        return matchingIngredients >= (recipeIngredients.size * 0.7)
    }

    /**
     * Verifica se deve mostrar lembrete baseado no horário das refeições
     */
    private suspend fun shouldShowMealTimeReminder(recipeId: String): Boolean {
        val currentHour = java.time.LocalTime.now().hour
        
        // Definir horários das refeições
        val isBreakfastTime = currentHour in 7..9
        val isLunchTime = currentHour in 11..13
        val isDinnerTime = currentHour in 18..20
        
        val recipe = getRecipeById(recipeId) ?: return false
        
        // Determinar tipo de refeição baseado no nome ou tags
        val recipeName = recipe.nome.lowercase()
        val tags = recipe.tags.map { it.lowercase() }
        
        val isBreakfastRecipe = recipeName.contains("café") || recipeName.contains("pão") ||
                tags.any { it.contains("café") || it.contains("café da manhã") }
        
        val isLunchRecipe = recipeName.contains("almoço") || recipeName.contains("prato") ||
                tags.any { it.contains("almoço") || it.contains("prato principal") }
        
        val isDinnerRecipe = recipeName.contains("jantar") || recipeName.contains("ceia") ||
                tags.any { it.contains("jantar") || it.contains("ceia") }
        
        return when {
            isBreakfastTime && isBreakfastRecipe -> true
            isLunchTime && isLunchRecipe -> true
            isDinnerTime && isDinnerRecipe -> true
            else -> false
        }
    }

    /**
     * Obtém a receita pelo ID
     */
    private suspend fun getRecipeById(recipeId: String): ReceitaEntity? {
        return try {
            receitaDao.getReceitaById(recipeId)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Simula obtenção de ingredientes disponíveis
     */
    private fun getAvailableIngredients(): List<String> {
        // Em uma implementação real, isso viria do banco de dados do usuário
        return listOf(
            "ovos", "leite", "farinha", "açúcar", "sal", "azeite",
            "cebola", "alho", "tomate", "frango", "carne", "arroz"
        )
    }

    /**
     * Obtém o último horário que a receita foi visualizada
     */
    private fun getLastViewedTime(recipeId: String): Long {
        // Em uma implementação real, isso viria do banco de dados
        return System.currentTimeMillis() - (Math.random() * 10 * 24 * 60 * 60 * 1000).toLong()
    }

    /**
     * Mostra a notificação
     */
    private fun showNotification(recipe: ReceitaEntity, notificationType: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Criar canal de notificação para Android 8.0+
        createNotificationChannel(notificationManager, notificationType)
        
        val (title, content, channelId) = when (notificationType) {
            TYPE_FAVORITE_REMINDER -> {
                Triple(
                    "Que tal preparar ${recipe.nome}?",
                    "Sua receita favorita está esperando por você!",
                    CHANNEL_ID_FAVORITES
                )
            }
            TYPE_INGREDIENT_REMINDER -> {
                Triple(
                    "Ingredientes disponíveis para ${recipe.nome}",
                    "Você tem os ingredientes necessários para preparar esta receita!",
                    CHANNEL_ID_INGREDIENTS
                )
            }
            TYPE_MEAL_TIME_REMINDER -> {
                Triple(
                    "Hora de cozinhar: ${recipe.nome}",
                    "Perfeita para este horário!",
                    CHANNEL_ID_RECIPE_REMINDERS
                )
            }
            else -> {
                Triple(
                    "Lembrete de receita",
                    "Que tal preparar ${recipe.nome}?",
                    CHANNEL_ID_RECIPE_REMINDERS
                )
            }
        }
        
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_reminder)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
        
        notificationManager.notify(recipe.id.hashCode(), builder.build())
    }

    /**
     * Cria o canal de notificação
     */
    private fun createNotificationChannel(notificationManager: NotificationManager, notificationType: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = when (notificationType) {
                TYPE_FAVORITE_REMINDER -> CHANNEL_ID_FAVORITES
                TYPE_INGREDIENT_REMINDER -> CHANNEL_ID_INGREDIENTS
                else -> CHANNEL_ID_RECIPE_REMINDERS
            }
            
            val channelName = when (notificationType) {
                TYPE_FAVORITE_REMINDER -> "Favoritos"
                TYPE_INGREDIENT_REMINDER -> "Ingredientes"
                else -> "Lembretes de Receitas"
            }
            
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Lembretes inteligentes de receitas"
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
} 