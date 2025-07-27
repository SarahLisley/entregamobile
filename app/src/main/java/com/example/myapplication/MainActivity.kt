package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import com.example.myapplication.ui.screens.MainNav
import com.example.myapplication.ui.theme.Theme
import com.example.myapplication.core.data.repository.UserPreferencesRepository
import com.example.myapplication.core.data.repository.DataSeeder
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.data.repository.NutritionRepository
import com.example.myapplication.core.data.database.AppDatabase
import com.example.myapplication.core.data.network.ConnectivityObserver
import com.example.myapplication.core.data.storage.ImageStorageService
import com.example.myapplication.core.ui.error.ErrorHandler
import com.example.myapplication.workers.SyncWorker
import com.example.myapplication.data.GeminiNutritionService
// import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// import javax.inject.Inject


fun isNightMode(): Boolean {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return hour >= 18 || hour < 6
}

// @AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // @Inject
    // lateinit var userPreferencesRepository: UserPreferencesRepository
    
    // @Inject
    // lateinit var dataSeeder: DataSeeder
    
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var dataSeeder: DataSeeder
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar dependências manualmente
        userPreferencesRepository = UserPreferencesRepository(this)
        
        // Criar instâncias dos repositórios necessários
        val database = AppDatabase.getDatabase(this)
        val connectivityObserver = ConnectivityObserver(this)
        val imageStorageService = ImageStorageService()
        val errorHandler = ErrorHandler()
        val receitasRepository = ReceitasRepository(
            database.receitaDao(),
            database.nutritionDataDao(),
            connectivityObserver,
            imageStorageService,
            errorHandler
        )
        val nutritionRepository = NutritionRepository(this, GeminiNutritionService())
        
        dataSeeder = DataSeeder(this, receitasRepository, nutritionRepository)
        
        // Configurar sincronização periódica com WorkManager
        configurarSincronizacao()
        
        // Popular banco de dados com receitas predefinidas (apenas uma vez)
        seedDatabaseIfNeeded()
        
        setContent {
            val darkModeEnabled by userPreferencesRepository.isDarkModeEnabled.collectAsState(initial = isNightMode())
            Theme(darkTheme = darkModeEnabled) {
                MainNav()
            }
        }
    }
    
    private fun seedDatabaseIfNeeded() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dataSeeder.seedDatabaseIfNeeded()
            } catch (e: Exception) {
                // Log error but don't crash the app
                e.printStackTrace()
            }
        }
    }
    
    private fun configurarSincronizacao() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            2, TimeUnit.HOURS // Sincronizar a cada 2 horas para economizar bateria e dados
        )
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(this).enqueue(syncWorkRequest)
    }
}
