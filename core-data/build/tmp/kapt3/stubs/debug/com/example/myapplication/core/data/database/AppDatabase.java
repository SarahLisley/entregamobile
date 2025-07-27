package com.example.myapplication.core.data.database;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \t2\u00020\u0001:\u0001\tB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&\u00a8\u0006\n"}, d2 = {"Lcom/example/myapplication/core/data/database/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "nutritionCacheDao", "Lcom/example/myapplication/core/data/database/dao/NutritionCacheDao;", "nutritionDataDao", "Lcom/example/myapplication/core/data/database/dao/NutritionDataDao;", "receitaDao", "Lcom/example/myapplication/core/data/database/dao/ReceitaDao;", "Companion", "core-data_debug"})
@androidx.room.Database(entities = {com.example.myapplication.core.data.database.entity.ReceitaEntity.class, com.example.myapplication.core.data.database.entity.NutritionCacheEntity.class, com.example.myapplication.core.data.database.entity.NutritionDataEntity.class}, version = 1, exportSchema = false)
@androidx.room.TypeConverters(value = {com.example.myapplication.core.data.database.converters.Converters.class})
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.example.myapplication.core.data.database.AppDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.myapplication.core.data.database.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.myapplication.core.data.database.dao.ReceitaDao receitaDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.myapplication.core.data.database.dao.NutritionCacheDao nutritionCacheDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.myapplication.core.data.database.dao.NutritionDataDao nutritionDataDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/myapplication/core/data/database/AppDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/example/myapplication/core/data/database/AppDatabase;", "getDatabase", "context", "Landroid/content/Context;", "core-data_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.myapplication.core.data.database.AppDatabase getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}