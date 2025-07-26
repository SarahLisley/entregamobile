package com.example.myapplication.core.data.di

import android.content.Context
import com.example.myapplication.core.data.storage.ImageStorageService
import com.example.myapplication.core.data.network.RetryInterceptor
import com.example.myapplication.core.ui.error.ErrorHandler
import com.example.myapplication.core.data.database.AppDatabase
import com.example.myapplication.core.data.network.ConnectivityObserver
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.data.repository.NutritionRepository
import com.example.myapplication.core.data.repository.UserPreferencesRepository
// import dagger.Module
// import dagger.Provides
// import dagger.hilt.InstallIn
// import dagger.hilt.android.qualifiers.ApplicationContext
// import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// import javax.inject.Singleton

// @Module
// @InstallIn(SingletonComponent::class)
object DataModule {
    
    // @Provides
    // @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    // @Provides
    // @Singleton
    fun provideConnectivityObserver(context: Context): ConnectivityObserver {
        return ConnectivityObserver(context)
    }
    
    // @Provides
    // @Singleton
    fun provideReceitasRepository(
        database: AppDatabase,
        connectivityObserver: ConnectivityObserver,
        imageStorageService: ImageStorageService,
        errorHandler: ErrorHandler
    ): ReceitasRepository {
        return ReceitasRepository(database.receitaDao(), connectivityObserver, imageStorageService, errorHandler)
    }
    
    // @Provides
    // @Singleton
    fun provideNutritionRepository(context: Context): NutritionRepository {
        return NutritionRepository(context)
    }
    
    // @Provides
    // @Singleton
    fun provideUserPreferencesRepository(context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }
    
    // @Provides
    // @Singleton
    fun provideDataSeeder(
        context: Context,
        receitasRepository: ReceitasRepository,
        nutritionRepository: NutritionRepository
    ): com.example.myapplication.core.data.repository.DataSeeder {
        return com.example.myapplication.core.data.repository.DataSeeder(context, receitasRepository, nutritionRepository)
    }
    
    // @Provides
    // @Singleton
    fun provideRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor()
    }
    
    // @Provides
    // @Singleton
    fun provideOkHttpClient(retryInterceptor: RetryInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(retryInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }
    
    // @Provides
    // @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
} 