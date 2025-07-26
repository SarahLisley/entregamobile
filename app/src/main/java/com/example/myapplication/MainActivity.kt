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
import com.example.myapplication.workers.SyncWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit


fun isNightMode(): Boolean {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return hour >= 18 || hour < 6
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar sincronização periódica com WorkManager
        configurarSincronizacao()
        
        // Create repository outside of Compose to avoid LocalContext.current issues
        val repo = UserPreferencesRepository(this)
        
        setContent {
            val darkModeEnabled by repo.isDarkModeEnabled.collectAsState(initial = isNightMode())
            Theme(darkTheme = darkModeEnabled) {
                MainNav()
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
