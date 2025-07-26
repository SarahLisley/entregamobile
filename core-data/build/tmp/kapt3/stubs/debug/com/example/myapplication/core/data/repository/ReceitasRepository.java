package com.example.myapplication.core.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010$\n\u0002\b\u000e\b\u0007\u0018\u00002\u00020\u0001B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ2\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\u0013H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J2\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00110\u0013H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0018\u0010\u0015J.\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\u00112\b\u0010\u001a\u001a\u0004\u0018\u00010\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001b\u0010\u001cJ \u0010\u001d\u001a\u00020\u000f2\u0018\u0010\u001e\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u0013\u0012\u0004\u0012\u00020\u000f0\u001fJ\u0018\u0010!\u001a\u0004\u0018\u00010 2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\"J\u0012\u0010#\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u00130$J\u0014\u0010%\u001a\b\u0012\u0004\u0012\u00020 0\u0013H\u0086@\u00a2\u0006\u0002\u0010&J\u0016\u0010\'\u001a\u00020\u000f2\u0006\u0010(\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\"J\u008e\u0001\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00110\u000e2\u0006\u0010*\u001a\u00020+2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010,\u001a\u00020\u00112\u0006\u0010-\u001a\u00020\u00112\b\u0010.\u001a\u0004\u0018\u00010/2\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u00110\u00132\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00110\u00132\u0006\u00102\u001a\u00020\u00112\u0006\u00103\u001a\u0002042\u0006\u00105\u001a\u00020\u00112\b\u00106\u001a\u0004\u0018\u00010\u00112\b\u00107\u001a\u0004\u0018\u00010\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b8\u00109J2\u0010:\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0014\u0010;\u001a\u0010\u0012\u0004\u0012\u00020\u0011\u0012\u0006\u0012\u0004\u0018\u00010\u00010<H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b=\u0010>J\u001c\u0010?\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b@\u0010&J0\u0010A\u001a\u00020\u00112\u0006\u0010*\u001a\u00020+2\u0006\u0010B\u001a\u00020/2\b\u0010C\u001a\u0004\u0018\u00010\u00112\u0006\u0010D\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010EJ$\u0010F\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010G\u001a\u00020 H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bH\u0010IR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006J"}, d2 = {"Lcom/example/myapplication/core/data/repository/ReceitasRepository;", "", "receitaDao", "Lcom/example/myapplication/core/data/database/dao/ReceitaDao;", "connectivityObserver", "Lcom/example/myapplication/core/data/network/ConnectivityObserver;", "imageStorageService", "Lcom/example/myapplication/core/data/storage/ImageStorageService;", "errorHandler", "Lcom/example/myapplication/core/ui/error/ErrorHandler;", "(Lcom/example/myapplication/core/data/database/dao/ReceitaDao;Lcom/example/myapplication/core/data/network/ConnectivityObserver;Lcom/example/myapplication/core/data/storage/ImageStorageService;Lcom/example/myapplication/core/ui/error/ErrorHandler;)V", "db", "Lcom/google/firebase/database/DatabaseReference;", "atualizarCurtidas", "Lkotlin/Result;", "", "id", "", "curtidas", "", "atualizarCurtidas-0E7RQCE", "(Ljava/lang/String;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "atualizarFavoritos", "favoritos", "atualizarFavoritos-0E7RQCE", "deletarReceita", "imageUrl", "deletarReceita-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "escutarReceitas", "onChange", "Lkotlin/Function1;", "Lcom/example/myapplication/core/data/database/entity/ReceitaEntity;", "getReceitaById", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getReceitas", "Lkotlinx/coroutines/flow/Flow;", "getUnsyncedReceitas", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markAsSynced", "receitaId", "salvarReceita", "context", "Landroid/content/Context;", "nome", "descricaoCurta", "imagemUri", "Landroid/net/Uri;", "ingredientes", "modoPreparo", "tempoPreparo", "porcoes", "", "userId", "userEmail", "imagemUrl", "salvarReceita-1iavgos", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/net/Uri;Ljava/util/List;Ljava/util/List;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "salvarReceitaNoFirebase", "receitaMap", "", "salvarReceitaNoFirebase-gIAlu-s", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sincronizarComFirebase", "sincronizarComFirebase-IoAF18A", "updateImage", "newImageUri", "oldImageUrl", "imageId", "(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateReceita", "receita", "updateReceita-gIAlu-s", "(Lcom/example/myapplication/core/data/database/entity/ReceitaEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core-data_debug"})
public final class ReceitasRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.database.dao.ReceitaDao receitaDao = null;
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
    com.example.myapplication.core.data.network.ConnectivityObserver connectivityObserver, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.storage.ImageStorageService imageStorageService, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.ui.error.ErrorHandler errorHandler) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity>> getReceitas() {
        return null;
    }
    
    public final void escutarReceitas(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity>, kotlin.Unit> onChange) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getUnsyncedReceitas(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object markAsSynced(@org.jetbrains.annotations.NotNull()
    java.lang.String receitaId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getReceitaById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.myapplication.core.data.database.entity.ReceitaEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateImage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri newImageUri, @org.jetbrains.annotations.Nullable()
    java.lang.String oldImageUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String imageId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
}