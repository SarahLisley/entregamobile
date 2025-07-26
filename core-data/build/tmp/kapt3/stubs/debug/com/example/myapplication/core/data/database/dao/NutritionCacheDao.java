package com.example.myapplication.core.data.database.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u000e\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0013"}, d2 = {"Lcom/example/myapplication/core/data/database/dao/NutritionCacheDao;", "", "deleteNutrition", "", "nutrition", "Lcom/example/myapplication/core/data/database/entity/NutritionCacheEntity;", "(Lcom/example/myapplication/core/data/database/entity/NutritionCacheEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteOldCache", "timestamp", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCacheSize", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getNutritionByTitle", "recipeTitle", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertNutrition", "core-data_debug"})
@androidx.room.Dao()
public abstract interface NutritionCacheDao {
    
    @androidx.room.Query(value = "SELECT * FROM nutrition_cache WHERE recipeTitle = :recipeTitle")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getNutritionByTitle(@org.jetbrains.annotations.NotNull()
    java.lang.String recipeTitle, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.myapplication.core.data.database.entity.NutritionCacheEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertNutrition(@org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.database.entity.NutritionCacheEntity nutrition, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteNutrition(@org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.database.entity.NutritionCacheEntity nutrition, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM nutrition_cache WHERE timestamp < :timestamp")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOldCache(long timestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM nutrition_cache")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCacheSize(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}