package com.example.myapplication.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.core.data.database.dao.NutritionCacheDao
import com.example.myapplication.core.data.database.dao.NutritionDataDao
import com.example.myapplication.core.data.database.dao.ReceitaDao
import com.example.myapplication.core.data.database.entity.NutritionCacheEntity
import com.example.myapplication.core.data.database.entity.NutritionDataEntity
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import com.example.myapplication.core.data.database.converters.Converters

@Database(
    entities = [
        ReceitaEntity::class,
        NutritionCacheEntity::class,
        NutritionDataEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receitaDao(): ReceitaDao
    abstract fun nutritionCacheDao(): NutritionCacheDao
    abstract fun nutritionDataDao(): NutritionDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migração de versão 1 para 2: Adicionar campo tags à tabela receitas
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Adicionar coluna tags se não existir
                try {
                    database.execSQL("ALTER TABLE receitas ADD COLUMN tags TEXT DEFAULT '[]'")
                } catch (e: Exception) {
                    // Coluna já existe, ignorar erro
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutrilivre_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 