package com.example.myapplication.core.ui.error;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\t"}, d2 = {"Lcom/example/myapplication/core/ui/error/ErrorHandler;", "", "()V", "handleError", "Lcom/example/myapplication/core/ui/error/UserFriendlyError;", "throwable", "", "shouldRetry", "", "core-ui_debug"})
public final class ErrorHandler {
    
    @javax.inject.Inject()
    public ErrorHandler() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.myapplication.core.ui.error.UserFriendlyError handleError(@org.jetbrains.annotations.NotNull()
    java.lang.Throwable throwable) {
        return null;
    }
    
    public final boolean shouldRetry(@org.jetbrains.annotations.NotNull()
    java.lang.Throwable throwable) {
        return false;
    }
}