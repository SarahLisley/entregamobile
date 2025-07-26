package com.example.myapplication.core.data.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\"\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0007J6\u0010\b\u001a\u00020\u00032\b\b\u0001\u0010\t\u001a\u00020\u00052\b\b\u0003\u0010\n\u001a\u00020\u000b2\b\b\u0003\u0010\u0006\u001a\u00020\u00052\b\b\u0003\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ@\u0010\u000f\u001a\u00020\u00032\b\b\u0001\u0010\t\u001a\u00020\u00052\b\b\u0003\u0010\n\u001a\u00020\u000b2\b\b\u0003\u0010\u0006\u001a\u00020\u00052\b\b\u0003\u0010\f\u001a\u00020\r2\b\b\u0003\u0010\u0010\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0011\u00a8\u0006\u0012"}, d2 = {"Lcom/example/myapplication/core/data/network/NutritionApiService;", "", "getNutritionInfo", "Lcom/example/myapplication/core/data/model/NutritionResponse;", "title", "", "apiKey", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchRecipes", "query", "addNutrition", "", "number", "", "(Ljava/lang/String;ZLjava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchRecipesWithNutrition", "instructionsRequired", "(Ljava/lang/String;ZLjava/lang/String;IZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core-data_debug"})
public abstract interface NutritionApiService {
    
    @retrofit2.http.GET(value = "recipes/guessNutrition")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getNutritionInfo(@retrofit2.http.Query(value = "title")
    @org.jetbrains.annotations.NotNull()
    java.lang.String title, @retrofit2.http.Query(value = "apiKey")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.myapplication.core.data.model.NutritionResponse> $completion);
    
    @retrofit2.http.GET(value = "recipes/complexSearch")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchRecipes(@retrofit2.http.Query(value = "query")
    @org.jetbrains.annotations.NotNull()
    java.lang.String query, @retrofit2.http.Query(value = "addRecipeNutrition")
    boolean addNutrition, @retrofit2.http.Query(value = "apiKey")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @retrofit2.http.Query(value = "number")
    int number, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.myapplication.core.data.model.NutritionResponse> $completion);
    
    @retrofit2.http.GET(value = "recipes/complexSearch")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchRecipesWithNutrition(@retrofit2.http.Query(value = "query")
    @org.jetbrains.annotations.NotNull()
    java.lang.String query, @retrofit2.http.Query(value = "addRecipeNutrition")
    boolean addNutrition, @retrofit2.http.Query(value = "apiKey")
    @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, @retrofit2.http.Query(value = "number")
    int number, @retrofit2.http.Query(value = "instructionsRequired")
    boolean instructionsRequired, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.myapplication.core.data.model.NutritionResponse> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}