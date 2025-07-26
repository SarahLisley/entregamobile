package com.example.myapplication.core.data.di;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u0006J\u001e\u0010\t\u001a\u00020\n2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J&\u0010\u0014\u001a\u00020\f2\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0011J\u0006\u0010\u001e\u001a\u00020\u0013J\u000e\u0010\u001f\u001a\u00020 2\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006!"}, d2 = {"Lcom/example/myapplication/core/data/di/DataModule;", "", "()V", "provideAppDatabase", "Lcom/example/myapplication/core/data/database/AppDatabase;", "context", "Landroid/content/Context;", "provideConnectivityObserver", "Lcom/example/myapplication/core/data/network/ConnectivityObserver;", "provideDataSeeder", "Lcom/example/myapplication/core/data/repository/DataSeeder;", "receitasRepository", "Lcom/example/myapplication/core/data/repository/ReceitasRepository;", "nutritionRepository", "Lcom/example/myapplication/core/data/repository/NutritionRepository;", "provideNutritionRepository", "provideOkHttpClient", "Lokhttp3/OkHttpClient;", "retryInterceptor", "Lcom/example/myapplication/core/data/network/RetryInterceptor;", "provideReceitasRepository", "database", "connectivityObserver", "imageStorageService", "Lcom/example/myapplication/core/data/storage/ImageStorageService;", "errorHandler", "Lcom/example/myapplication/core/ui/error/ErrorHandler;", "provideRetrofit", "Lretrofit2/Retrofit;", "okHttpClient", "provideRetryInterceptor", "provideUserPreferencesRepository", "Lcom/example/myapplication/core/data/repository/UserPreferencesRepository;", "core-data_debug"})
public final class DataModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.myapplication.core.data.di.DataModule INSTANCE = null;
    
    private DataModule() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.myapplication.core.data.database.AppDatabase provideAppDatabase(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.myapplication.core.data.network.ConnectivityObserver provideConnectivityObserver(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.myapplication.core.data.repository.ReceitasRepository provideReceitasRepository(@org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.database.AppDatabase database, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.network.ConnectivityObserver connectivityObserver, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.storage.ImageStorageService imageStorageService, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.ui.error.ErrorHandler errorHandler) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.myapplication.core.data.repository.NutritionRepository provideNutritionRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.myapplication.core.data.repository.UserPreferencesRepository provideUserPreferencesRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.myapplication.core.data.repository.DataSeeder provideDataSeeder(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.repository.ReceitasRepository receitasRepository, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.repository.NutritionRepository nutritionRepository) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.myapplication.core.data.network.RetryInterceptor provideRetryInterceptor() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.OkHttpClient provideOkHttpClient(@org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.network.RetryInterceptor retryInterceptor) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final retrofit2.Retrofit provideRetrofit(@org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient okHttpClient) {
        return null;
    }
}