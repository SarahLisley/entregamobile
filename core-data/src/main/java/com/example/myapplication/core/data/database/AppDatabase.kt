package com.example.myapplication.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.core.data.database.dao.ReceitaDao
import com.example.myapplication.core.data.database.dao.NutritionCacheDao
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.database.entity.NutritionCacheEntity
import com.example.myapplication.core.data.database.converters.Converters

@Database(
    entities = [
        ReceitaEntity::class,
        NutritionCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun receitaDao(): ReceitaDao
    abstract fun nutritionCacheDao(): NutritionCacheDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutri_livre_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 