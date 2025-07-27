package com.example.myapplication.core.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u000e\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u000fJ$\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u0015\u001a\u00020\u0016H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0017\u0010\u0018J$\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u001a\u001a\u00020\u001bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0018\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010 \u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0018J$\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u0015\u001a\u00020\u0016H\u0082@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\"\u0010\u0018J$\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u001a\u001a\u00020\u001bH\u0082@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b$\u0010\u001dR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006%"}, d2 = {"Lcom/example/myapplication/core/data/repository/NutritionRepository;", "", "context", "Landroid/content/Context;", "nutritionService", "Lcom/example/myapplication/core/data/network/NutritionService;", "(Landroid/content/Context;Lcom/example/myapplication/core/data/network/NutritionService;)V", "database", "Lcom/example/myapplication/core/data/database/AppDatabase;", "nutritionCacheDao", "Lcom/example/myapplication/core/data/database/dao/NutritionCacheDao;", "nutritionDataDao", "Lcom/example/myapplication/core/data/database/dao/NutritionDataDao;", "cleanOldCache", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCacheStats", "", "getNutritionInfo", "Lkotlin/Result;", "Lcom/example/myapplication/core/data/model/RecipeNutrition;", "recipeTitle", "", "getNutritionInfo-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getNutritionInfoForRecipe", "receita", "Lcom/example/myapplication/core/data/database/entity/ReceitaEntity;", "getNutritionInfoForRecipe-gIAlu-s", "(Lcom/example/myapplication/core/data/database/entity/ReceitaEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSavedNutritionData", "Lcom/example/myapplication/core/data/database/entity/NutritionDataEntity;", "receitaId", "searchFromGeminiAPI", "searchFromGeminiAPI-gIAlu-s", "searchFromGeminiAPIForRecipe", "searchFromGeminiAPIForRecipe-gIAlu-s", "core-data_debug"})
public final class NutritionRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.network.NutritionService nutritionService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.database.AppDatabase database = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.database.dao.NutritionCacheDao nutritionCacheDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.database.dao.NutritionDataDao nutritionDataDao = null;
    
    public NutritionRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.network.NutritionService nutritionService) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object cleanOldCache(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCacheStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getSavedNutritionData(@org.jetbrains.annotations.NotNull()
    java.lang.String receitaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.myapplication.core.data.database.entity.NutritionDataEntity> $completion) {
        return null;
    }
}