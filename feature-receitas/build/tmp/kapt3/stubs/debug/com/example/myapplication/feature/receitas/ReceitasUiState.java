package com.example.myapplication.feature.receitas;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0003\u0003\u0004\u0005B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0003\u0006\u0007\b\u00a8\u0006\t"}, d2 = {"Lcom/example/myapplication/feature/receitas/ReceitasUiState;", "", "()V", "Error", "Loading", "Success", "Lcom/example/myapplication/feature/receitas/ReceitasUiState$Error;", "Lcom/example/myapplication/feature/receitas/ReceitasUiState$Loading;", "Lcom/example/myapplication/feature/receitas/ReceitasUiState$Success;", "feature-receitas_debug"})
public abstract class ReceitasUiState {
    
    private ReceitasUiState() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/example/myapplication/feature/receitas/ReceitasUiState$Error;", "Lcom/example/myapplication/feature/receitas/ReceitasUiState;", "error", "Lcom/example/myapplication/core/ui/error/UserFriendlyError;", "(Lcom/example/myapplication/core/ui/error/UserFriendlyError;)V", "getError", "()Lcom/example/myapplication/core/ui/error/UserFriendlyError;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "feature-receitas_debug"})
    public static final class Error extends com.example.myapplication.feature.receitas.ReceitasUiState {
        @org.jetbrains.annotations.NotNull()
        private final com.example.myapplication.core.ui.error.UserFriendlyError error = null;
        
        public Error(@org.jetbrains.annotations.NotNull()
        com.example.myapplication.core.ui.error.UserFriendlyError error) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.myapplication.core.ui.error.UserFriendlyError getError() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.myapplication.core.ui.error.UserFriendlyError component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.myapplication.feature.receitas.ReceitasUiState.Error copy(@org.jetbrains.annotations.NotNull()
        com.example.myapplication.core.ui.error.UserFriendlyError error) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/example/myapplication/feature/receitas/ReceitasUiState$Loading;", "Lcom/example/myapplication/feature/receitas/ReceitasUiState;", "()V", "feature-receitas_debug"})
    public static final class Loading extends com.example.myapplication.feature.receitas.ReceitasUiState {
        @org.jetbrains.annotations.NotNull()
        public static final com.example.myapplication.feature.receitas.ReceitasUiState.Loading INSTANCE = null;
        
        private Loading() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u000f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u0019\u0010\t\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/example/myapplication/feature/receitas/ReceitasUiState$Success;", "Lcom/example/myapplication/feature/receitas/ReceitasUiState;", "receitas", "", "Lcom/example/myapplication/core/data/database/entity/ReceitaEntity;", "(Ljava/util/List;)V", "getReceitas", "()Ljava/util/List;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "feature-receitas_debug"})
    public static final class Success extends com.example.myapplication.feature.receitas.ReceitasUiState {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity> receitas = null;
        
        public Success(@org.jetbrains.annotations.NotNull()
        java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity> receitas) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity> getReceitas() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity> component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.myapplication.feature.receitas.ReceitasUiState.Success copy(@org.jetbrains.annotations.NotNull()
        java.util.List<com.example.myapplication.core.data.database.entity.ReceitaEntity> receitas) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}