package com.example.myapplication.core.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u009c\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0017\b\u0007\u0018\u00002\u00020\u0001B/\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ$\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0012\u001a\u00020\u0013H\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J2\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0017\u001a\u00020\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00180\u001aH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001b\u0010\u001cJ2\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0017\u001a\u00020\u00182\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00180\u001aH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001f\u0010\u001cJ\u001a\u0010 \u001a\u00020!2\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010\"\u001a\u0004\u0018\u00010\u0018H\u0016J\u001a\u0010#\u001a\u00020!2\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010\"\u001a\u0004\u0018\u00010\u0018H\u0016J$\u0010$\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u00182\u0012\u0010%\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\'0&H\u0002J:\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010)\u001a\u00020\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00180\u001aH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b*\u0010+J.\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0017\u001a\u00020\u00182\b\u0010-\u001a\u0004\u0018\u00010\u0018H\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b.\u0010/J$\u00100\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0017\u001a\u00020\u0018H\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b1\u00102J(\u00103\u001a\u00020\u00112\u0018\u00104\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u001a\u0012\u0004\u0012\u00020\u001105H\u0096@\u00a2\u0006\u0002\u00106J:\u00107\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010)\u001a\u00020\u00182\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00180\u001aH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b8\u0010+J\u0018\u00109\u001a\u0004\u0018\u00010:2\u0006\u0010;\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u00102J\u0018\u0010<\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0017\u001a\u00020\u0018H\u0096@\u00a2\u0006\u0002\u00102J\u0014\u0010=\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u001a0>H\u0016J,\u0010?\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010;\u001a\u00020\u00182\u0006\u0010@\u001a\u00020AH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bB\u0010CJ\u008e\u0001\u0010D\u001a\b\u0012\u0004\u0012\u00020\u00180\u00102\u0006\u0010E\u001a\u00020F2\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010G\u001a\u00020\u00182\u0006\u0010H\u001a\u00020\u00182\b\u0010I\u001a\u0004\u0018\u00010J2\f\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00180\u001a2\f\u0010L\u001a\b\u0012\u0004\u0012\u00020\u00180\u001a2\u0006\u0010M\u001a\u00020\u00182\u0006\u0010N\u001a\u00020O2\u0006\u0010)\u001a\u00020\u00182\b\u0010P\u001a\u0004\u0018\u00010\u00182\b\u0010Q\u001a\u0004\u0018\u00010\u0018H\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bR\u0010SJ2\u0010T\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0014\u0010U\u001a\u0010\u0012\u0004\u0012\u00020\u0018\u0012\u0006\u0012\u0004\u0018\u00010\'0&H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bV\u0010WJ\u001c\u0010X\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bY\u0010ZJ\u001c\u0010[\u001a\b\u0012\u0004\u0012\u00020O0\u0010H\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\\\u0010ZJ2\u0010]\u001a\u0004\u0018\u00010\u00182\u0006\u0010E\u001a\u00020F2\u0006\u0010^\u001a\u00020J2\b\u0010_\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0017\u001a\u00020\u0018H\u0096@\u00a2\u0006\u0002\u0010`J$\u0010a\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0012\u001a\u00020\u0013H\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bb\u0010\u0015J \u0010c\u001a\u0004\u0018\u00010\u00182\u0006\u0010d\u001a\u00020\u00182\u0006\u0010e\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010/R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006f"}, d2 = {"Lcom/example/myapplication/core/data/repository/ReceitasRepository;", "Lcom/example/myapplication/core/data/repository/IReceitasRepository;", "receitaDao", "Lcom/example/myapplication/core/data/database/dao/ReceitaDao;", "nutritionDataDao", "Lcom/example/myapplication/core/data/database/dao/NutritionDataDao;", "connectivityObserver", "Lcom/example/myapplication/core/data/network/ConnectivityObserver;", "imageStorageService", "Lcom/example/myapplication/core/data/storage/ImageStorageService;", "errorHandler", "Lcom/example/myapplication/core/ui/error/ErrorHandler;", "(Lcom/example/myapplication/core/data/database/dao/ReceitaDao;Lcom/example/myapplication/core/data/database/dao/NutritionDataDao;Lcom/example/myapplication/core/data/network/ConnectivityObserver;Lcom/example/myapplication/core/data/storage/ImageStorageService;Lcom/example/myapplication/core/ui/error/ErrorHandler;)V", "db", "Lcom/google/firebase/database/DatabaseReference;", "addReceita", "Lkotlin/Result;", "", "receita", "Lcom/example/myapplication/core/data/database/entity/ReceitaEntity;", "addReceita-gIAlu-s", "(Lcom/example/myapplication/core/data/database/entity/ReceitaEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "atualizarCurtidas", "id", "", "curtidas", "", "atualizarCurtidas-0E7RQCE", "(Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "atualizarFavoritos", "favoritos", "atualizarFavoritos-0E7RQCE", "canDeleteReceita", "", "currentUserId", "canEditReceita", "createReceitaFromFirebaseData", "data", "", "", "curtirReceita", "userId", "curtirReceita-BWLJW6A", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deletarReceita", "imageUrl", "deletarReceita-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteReceita", "deleteReceita-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "escutarReceitas", "callback", "Lkotlin/Function1;", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "favoritarReceita", "favoritarReceita-BWLJW6A", "getDadosNutricionais", "Lcom/example/myapplication/core/data/database/entity/NutritionDataEntity;", "receitaId", "getReceitaById", "getReceitas", "Lkotlinx/coroutines/flow/Flow;", "salvarDadosNutricionais", "nutritionData", "Lcom/example/myapplication/core/data/model/RecipeNutrition;", "salvarDadosNutricionais-0E7RQCE", "(Ljava/lang/String;Lcom/example/myapplication/core/data/model/RecipeNutrition;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "salvarReceita", "context", "Landroid/content/Context;", "nome", "descricaoCurta", "imagemUri", "Landroid/net/Uri;", "ingredientes", "modoPreparo", "tempoPreparo", "porcoes", "", "userEmail", "imagemUrl", "salvarReceita-1iavgos", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/net/Uri;Ljava/util/List;Ljava/util/List;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "salvarReceitaNoFirebase", "receitaMap", "salvarReceitaNoFirebase-gIAlu-s", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sincronizarComFirebase", "sincronizarComFirebase-IoAF18A", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "syncFromFirebase", "syncFromFirebase-IoAF18A", "updateImage", "novaImagemUri", "imagemUrlAntiga", "(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateReceita", "updateReceita-gIAlu-s", "uploadImagemParaSupabase", "base64Image", "fileName", "core-data_debug"})
public final class ReceitasRepository implements com.example.myapplication.core.data.repository.IReceitasRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.database.dao.ReceitaDao receitaDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.database.dao.NutritionDataDao nutritionDataDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.network.ConnectivityObserver connectivityObserver = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.storage.ImageStorageService imageStorageService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.ui.error.ErrorHandler errorHandler = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.database.DatabaseReference db = null;
    
    @javax.inject.Inject()
    public ReceitasRepository(@org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.database.dao.ReceitaDao receitaDao, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.database.dao.NutritionDataDao nutritionDataDao, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.network.ConnectivityObserver connectivityObserver, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.storage.ImageStorageService imageStorageService, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.ui.error.ErrorHandler errorHandler) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getDadosNutricionais(@org.jetbrains.annotations.NotNull()
    java.lang.String receitaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.myapplication.core.data.database.entity.NutritionDataEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object uploadImagemParaSupabase(@org.jetbrains.annotations.NotNull()
    java.lang.String base64Image, @org.jetbrains.annotations.NotNull()
    java.lang.String fileName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity>> getReceitas() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object escutarReceitas(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity>, kotlin.Unit> callback, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final com.example.myapplication.core.data.database.entity.ReceitaEntity createReceitaFromFirebaseData(java.lang.String id, java.util.Map<java.lang.String, ? extends java.lang.Object> data) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getReceitaById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.myapplication.core.data.database.entity.ReceitaEntity> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateImage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri novaImagemUri, @org.jetbrains.annotations.Nullable()
    java.lang.String imagemUrlAntiga, @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @java.lang.Override()
    public boolean canEditReceita(@org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.database.entity.ReceitaEntity receita, @org.jetbrains.annotations.Nullable()
    java.lang.String currentUserId) {
        return false;
    }
    
    @java.lang.Override()
    public boolean canDeleteReceita(@org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.database.entity.ReceitaEntity receita, @org.jetbrains.annotations.Nullable()
    java.lang.String currentUserId) {
        return false;
    }
}