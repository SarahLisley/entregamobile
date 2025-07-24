package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.myapplication.ui.screens.MainNav
import com.example.myapplication.ui.theme.Theme
import com.example.myapplication.data.UserPreferencesRepository
import java.util.Calendar


fun isNightMode(): Boolean {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return hour >= 18 || hour < 6
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Agendar sincronização periódica
        // WorkManagerHelper.scheduleProdutoSync(this)
        
        // Create repository outside of Compose to avoid LocalContext.current issues
        val repo = UserPreferencesRepository(this)
        
        setContent {
            val darkModeEnabled by repo.isDarkModeEnabled.collectAsState(initial = isNightMode())
            Theme(darkTheme = darkModeEnabled) {
                MainNav()
            }
        }
    }
}
