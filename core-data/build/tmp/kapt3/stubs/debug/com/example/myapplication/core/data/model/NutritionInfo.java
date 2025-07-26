package com.example.myapplication.core.data.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\u000e\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\b\u0012\b\u0010\n\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0002\u0010\u000bJ\u0011\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJ\u000b\u0010\u0017\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\bH\u00c6\u0003JP\u0010\u001a\u001a\u00020\u00002\u0010\b\u0002\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\bH\u00c6\u0001\u00a2\u0006\u0002\u0010\u001bJ\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001J\t\u0010!\u001a\u00020\bH\u00d6\u0001R\u001a\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0018\u0010\n\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0018\u0010\t\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u001e\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010\u00a8\u0006\""}, d2 = {"Lcom/example/myapplication/core/data/model/NutritionInfo;", "", "nutrients", "", "Lcom/example/myapplication/core/data/model/Nutrient;", "calories", "", "protein", "", "fat", "carbohydrates", "(Ljava/util/List;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCalories", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getCarbohydrates", "()Ljava/lang/String;", "getFat", "getNutrients", "()Ljava/util/List;", "getProtein", "component1", "component2", "component3", "component4", "component5", "copy", "(Ljava/util/List;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/example/myapplication/core/data/model/NutritionInfo;", "equals", "", "other", "hashCode", "", "toString", "core-data_debug"})
public final class NutritionInfo {
    @com.google.gson.annotations.SerializedName(value = "nutrients")
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<com.example.myapplication.core.data.model.Nutrient> nutrients = null;
    @com.google.gson.annotations.SerializedName(value = "calories")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double calories = null;
    @com.google.gson.annotations.SerializedName(value = "protein")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String protein = null;
    @com.google.gson.annotations.SerializedName(value = "fat")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String fat = null;
    @com.google.gson.annotations.SerializedName(value = "carbohydrates")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String carbohydrates = null;
    
    public NutritionInfo(@org.jetbrains.annotations.Nullable()
    java.util.List<com.example.myapplication.core.data.model.Nutrient> nutrients, @org.jetbrains.annotations.Nullable()
    java.lang.Double calories, @org.jetbrains.annotations.Nullable()
    java.lang.String protein, @org.jetbrains.annotations.Nullable()
    java.lang.String fat, @org.jetbrains.annotations.Nullable()
    java.lang.String carbohydrates) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.example.myapplication.core.data.model.Nutrient> getNutrients() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getCalories() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getProtein() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFat() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCarbohydrates() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.example.myapplication.core.data.model.Nutrient> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.myapplication.core.data.model.NutritionInfo copy(@org.jetbrains.annotations.Nullable()
    java.util.List<com.example.myapplication.core.data.model.Nutrient> nutrients, @org.jetbrains.annotations.Nullable()
    java.lang.Double calories, @org.jetbrains.annotations.Nullable()
    java.lang.String protein, @org.jetbrains.annotations.Nullable()
    java.lang.String fat, @org.jetbrains.annotations.Nullable()
    java.lang.String carbohydrates) {
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