package com.example.myapplication.feature.receitas;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0013\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJf\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u000b2\u0006\u0010 \u001a\u00020\u000b2\b\u0010!\u001a\u0004\u0018\u00010\"2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u000b0$2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u000b0$2\u0006\u0010&\u001a\u00020\u000b2\u0006\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020\u000b2\b\u0010*\u001a\u0004\u0018\u00010\u000bJ\u000e\u0010+\u001a\u00020\u001c2\u0006\u0010,\u001a\u00020\u000bJ\u0006\u0010-\u001a\u00020\u001cJ$\u0010.\u001a\u00020\u001c2\u0006\u0010/\u001a\u00020\u000b2\u0006\u0010)\u001a\u00020\u000b2\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u000b0$J\u0018\u00101\u001a\u00020\u001c2\u0006\u0010/\u001a\u00020\u000b2\b\u00102\u001a\u0004\u0018\u00010\u000bJf\u00103\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010/\u001a\u00020\u000b2\u0006\u0010\u001f\u001a\u00020\u000b2\u0006\u0010 \u001a\u00020\u000b2\b\u00104\u001a\u0004\u0018\u00010\"2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u000b0$2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u000b0$2\u0006\u0010&\u001a\u00020\u000b2\u0006\u0010\'\u001a\u00020(2\b\u00105\u001a\u0004\u0018\u00010\u000bJ$\u00106\u001a\u00020\u001c2\u0006\u0010/\u001a\u00020\u000b2\u0006\u0010)\u001a\u00020\u000b2\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u000b0$J\u0010\u00108\u001a\u00020\u000e2\u0006\u0010,\u001a\u00020\u000bH\u0002J\u0006\u00109\u001a\u00020\u001cJ\b\u0010:\u001a\u00020\u001cH\u0002R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00100\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0018\u00a8\u0006;"}, d2 = {"Lcom/example/myapplication/feature/receitas/ReceitasViewModel;", "Landroidx/lifecycle/ViewModel;", "receitasRepository", "Lcom/example/myapplication/core/data/repository/ReceitasRepository;", "nutritionRepository", "Lcom/example/myapplication/core/data/repository/NutritionRepository;", "errorHandler", "Lcom/example/myapplication/core/ui/error/ErrorHandler;", "(Lcom/example/myapplication/core/data/repository/ReceitasRepository;Lcom/example/myapplication/core/data/repository/NutritionRepository;Lcom/example/myapplication/core/ui/error/ErrorHandler;)V", "_eventChannel", "Lkotlinx/coroutines/channels/Channel;", "", "_nutritionState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/myapplication/core/data/model/RecipeNutrition;", "_uiState", "Lcom/example/myapplication/feature/receitas/ReceitasUiState;", "eventFlow", "Lkotlinx/coroutines/flow/Flow;", "getEventFlow", "()Lkotlinx/coroutines/flow/Flow;", "nutritionState", "Lkotlinx/coroutines/flow/StateFlow;", "getNutritionState", "()Lkotlinx/coroutines/flow/StateFlow;", "uiState", "getUiState", "adicionarReceita", "", "context", "Landroid/content/Context;", "nome", "descricaoCurta", "imagemUri", "Landroid/net/Uri;", "ingredientes", "", "modoPreparo", "tempoPreparo", "porcoes", "", "userId", "userEmail", "buscarInformacoesNutricionais", "recipeTitle", "carregarReceitas", "curtirReceita", "id", "curtidasAtuais", "deletarReceita", "imageUrl", "editarReceita", "novaImagemUri", "imagemUrlAntiga", "favoritarReceita", "favoritosAtuais", "getFallbackNutritionData", "limparInformacoesNutricionais", "sincronizarComFirebase", "feature-receitas_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ReceitasViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.repository.ReceitasRepository receitasRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.data.repository.NutritionRepository nutritionRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.myapplication.core.ui.error.ErrorHandler errorHandler = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.myapplication.feature.receitas.ReceitasUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.myapplication.feature.receitas.ReceitasUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.channels.Channel<java.lang.String> _eventChannel = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> eventFlow = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.myapplication.core.data.model.RecipeNutrition> _nutritionState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.myapplication.core.data.model.RecipeNutrition> nutritionState = null;
    
    @javax.inject.Inject()
    public ReceitasViewModel(@org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.repository.ReceitasRepository receitasRepository, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.data.repository.NutritionRepository nutritionRepository, @org.jetbrains.annotations.NotNull()
    com.example.myapplication.core.ui.error.ErrorHandler errorHandler) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.myapplication.feature.receitas.ReceitasUiState> getUiState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getEventFlow() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.myapplication.core.data.model.RecipeNutrition> getNutritionState() {
        return null;
    }
    
    public final void carregarReceitas() {
    }
    
    private final void sincronizarComFirebase() {
    }
    
    public final void adicionarReceita(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String nome, @org.jetbrains.annotations.NotNull()
    java.lang.String descricaoCurta, @org.jetbrains.annotations.Nullable()
    android.net.Uri imagemUri, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> ingredientes, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> modoPreparo, @org.jetbrains.annotations.NotNull()
    java.lang.String tempoPreparo, int porcoes, @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.Nullable()
    java.lang.String userEmail) {
    }
    
    public final void deletarReceita(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.Nullable()
    java.lang.String imageUrl) {
    }
    
    public final void curtirReceita(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> curtidasAtuais) {
    }
    
    public final void favoritarReceita(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> favoritosAtuais) {
    }
    
    public final void editarReceita(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String nome, @org.jetbrains.annotations.NotNull()
    java.lang.String descricaoCurta, @org.jetbrains.annotations.Nullable()
    android.net.Uri novaImagemUri, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> ingredientes, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> modoPreparo, @org.jetbrains.annotations.NotNull()
    java.lang.String tempoPreparo, int porcoes, @org.jetbrains.annotations.Nullable()
    java.lang.String imagemUrlAntiga) {
    }
    
    public final void buscarInformacoesNutricionais(@org.jetbrains.annotations.NotNull()
    java.lang.String recipeTitle) {
    }
    
    private final com.example.myapplication.core.data.model.RecipeNutrition getFallbackNutritionData(java.lang.String recipeTitle) {
        return null;
    }
    
    public final void limparInformacoesNutricionais() {
    }
}