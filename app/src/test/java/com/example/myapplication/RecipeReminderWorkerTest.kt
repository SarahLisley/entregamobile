package com.example.myapplication

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.myapplication.workers.RecipeReminderWorker
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class RecipeReminderWorkerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @Test
    fun testRecipeReminderWorkerSuccess() = runBlocking {
        // Criar dados de entrada para o worker
        val inputData = androidx.work.Data.Builder()
            .putString(RecipeReminderWorker.KEY_RECIPE_ID, "test_recipe_1")
            .putString(RecipeReminderWorker.KEY_NOTIFICATION_TYPE, RecipeReminderWorker.TYPE_FAVORITE_REMINDER)
            .build()

        // Criar o worker
        val worker = TestListenableWorkerBuilder<RecipeReminderWorker>(context)
            .setInputData(inputData)
            .build()

        // Executar o worker
        val result = worker.doWork()

        // Verificar se o resultado é sucesso
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun testRecipeReminderWorkerFailure() = runBlocking {
        // Criar dados de entrada inválidos (sem recipe_id)
        val inputData = androidx.work.Data.Builder()
            .putString(RecipeReminderWorker.KEY_NOTIFICATION_TYPE, RecipeReminderWorker.TYPE_FAVORITE_REMINDER)
            .build()

        // Criar o worker
        val worker = TestListenableWorkerBuilder<RecipeReminderWorker>(context)
            .setInputData(inputData)
            .build()

        // Executar o worker
        val result = worker.doWork()

        // Verificar se o resultado é falha
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun testRecipeReminderWorkerWithDifferentNotificationTypes() = runBlocking {
        val notificationTypes = listOf(
            RecipeReminderWorker.TYPE_FAVORITE_REMINDER,
            RecipeReminderWorker.TYPE_INGREDIENT_REMINDER,
            RecipeReminderWorker.TYPE_MEAL_TIME_REMINDER
        )

        notificationTypes.forEach { notificationType ->
            val inputData = androidx.work.Data.Builder()
                .putString(RecipeReminderWorker.KEY_RECIPE_ID, "test_recipe_$notificationType")
                .putString(RecipeReminderWorker.KEY_NOTIFICATION_TYPE, notificationType)
                .build()

            val worker = TestListenableWorkerBuilder<RecipeReminderWorker>(context)
                .setInputData(inputData)
                .build()

            val result = worker.doWork()
            
            // Verificar se o resultado é sucesso para todos os tipos
            assertEquals("Worker failed for notification type: $notificationType", 
                ListenableWorker.Result.success(), result)
        }
    }

    @Test
    fun testScheduleRecipeReminder() {
        val recipeId = "test_recipe_schedule"
        val notificationType = RecipeReminderWorker.TYPE_FAVORITE_REMINDER
        val delayMinutes = 30L

        // Agendar um lembrete
        RecipeReminderWorker.scheduleRecipeReminder(
            context = context,
            recipeId = recipeId,
            notificationType = notificationType,
            delayMinutes = delayMinutes
        )

        // Verificar se o trabalho foi agendado
        val workManager = androidx.work.WorkManager.getInstance(context)
        val workInfos = workManager.getWorkInfosForUniqueWork("${RecipeReminderWorker.WORK_NAME}_$recipeId")
        
        // Aguardar um pouco para o trabalho ser agendado
        Thread.sleep(1000)
        
        val workInfoList = workInfos.get()
        assertEquals("Work should be scheduled", 1, workInfoList.size)
    }

    @Test
    fun testSchedulePeriodicFavoriteReminders() {
        // Agendar lembretes periódicos
        RecipeReminderWorker.schedulePeriodicFavoriteReminders(context)

        // Verificar se o trabalho periódico foi agendado
        val workManager = androidx.work.WorkManager.getInstance(context)
        val workInfos = workManager.getWorkInfosForUniqueWork("periodic_recipe_reminders")
        
        // Aguardar um pouco para o trabalho ser agendado
        Thread.sleep(1000)
        
        val workInfoList = workInfos.get()
        assertEquals("Periodic work should be scheduled", 1, workInfoList.size)
    }
} 