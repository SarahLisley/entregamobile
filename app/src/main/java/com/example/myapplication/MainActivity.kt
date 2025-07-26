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
import com.example.myapplication.data.UserPreferencesRepository
import com.example.myapplication.data.DataSeeder
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.ConnectivityObserver
import com.example.myapplication.data.ReceitasRepository
import com.example.myapplication.data.NutritionRepository
import com.example.myapplication.workers.SyncWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun isNightMode(): Boolean {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return hour >= 18 || hour < 6
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar sincronização periódica com WorkManager
        configurarSincronizacao()
        
        // Popular banco de dados com receitas predefinidas (apenas uma vez)
        seedDatabaseIfNeeded()
        
        // Create repository outside of Compose to avoid LocalContext.current issues
        val repo = UserPreferencesRepository(this)
        
        setContent {
            val darkModeEnabled by repo.isDarkModeEnabled.collectAsState(initial = isNightMode())
            Theme(darkTheme = darkModeEnabled) {
                MainNav()
            }
        }
    }
    
    private fun seedDatabaseIfNeeded() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(this@MainActivity)
                val receitaDao = database.receitaDao()
                val connectivityObserver = ConnectivityObserver(this@MainActivity)
                val receitasRepository = ReceitasRepository(receitaDao, connectivityObserver)
                val nutritionRepository = NutritionRepository()
                
                val dataSeeder = DataSeeder(this@MainActivity, receitasRepository, nutritionRepository)
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
            15, TimeUnit.MINUTES // Sincronizar a cada 15 minutos
        )
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(this).enqueue(syncWorkRequest)
    }
}
