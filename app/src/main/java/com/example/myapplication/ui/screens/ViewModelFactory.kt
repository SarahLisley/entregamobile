package com.example.myapplication.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.core.data.database.AppDatabase
import com.example.myapplication.core.data.network.GeminiServiceImpl
import com.example.myapplication.core.data.repository.AuthRepository
import com.example.myapplication.core.data.repository.NutritionRepository
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.ui.error.ErrorHandler
import com.example.myapplication.feature.receitas.ReceitasViewModel
import com.example.myapplication.feature.receitas.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ReceitasViewModel::class.java) -> {
                val database = AppDatabase.getDatabase(context)
                val receitasRepository = ReceitasRepository(
                    receitaDao = database.receitaDao(),
                    nutritionDataDao = database.nutritionDataDao(),
                    connectivityObserver = com.example.myapplication.core.data.network.ConnectivityObserver(context),
                    imageStorageService = com.example.myapplication.core.data.storage.ImageStorageService(),
                    errorHandler = ErrorHandler()
                )
                val nutritionRepository = NutritionRepository(
                    context = context,
                    nutritionService = GeminiServiceImpl("AIzaSyDiwB3lig9_fvI5wbBlILl32Ztqj41XO2I")
                )
                val errorHandler = ErrorHandler()
                
                // Obter informações do usuário logado
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val authRepository = AuthRepository(auth)
                
                ReceitasViewModel(
                    receitasRepository, 
                    nutritionRepository, 
                    errorHandler,
                    authRepository
                ) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                val database = AppDatabase.getDatabase(context)
                val receitasRepository = ReceitasRepository(
                    receitaDao = database.receitaDao(),
                    nutritionDataDao = database.nutritionDataDao(),
                    connectivityObserver = com.example.myapplication.core.data.network.ConnectivityObserver(context),
                    imageStorageService = com.example.myapplication.core.data.storage.ImageStorageService(),
                    errorHandler = ErrorHandler()
                )
                val chatService = GeminiServiceImpl("AIzaSyDiwB3lig9_fvI5wbBlILl32Ztqj41XO2I")
                val auth = FirebaseAuth.getInstance()
                val authRepository = AuthRepository(auth)
                
                ChatViewModel(
                    chatService,
                    receitasRepository,
                    authRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 