package com.example.myapplication.feature.receitas;

import com.example.myapplication.core.data.repository.NutritionRepository;
import com.example.myapplication.core.data.repository.ReceitasRepository;
import com.example.myapplication.core.ui.error.ErrorHandler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class ReceitasViewModel_Factory implements Factory<ReceitasViewModel> {
  private final Provider<ReceitasRepository> receitasRepositoryProvider;

  private final Provider<NutritionRepository> nutritionRepositoryProvider;

  private final Provider<ErrorHandler> errorHandlerProvider;

  public ReceitasViewModel_Factory(Provider<ReceitasRepository> receitasRepositoryProvider,
      Provider<NutritionRepository> nutritionRepositoryProvider,
      Provider<ErrorHandler> errorHandlerProvider) {
    this.receitasRepositoryProvider = receitasRepositoryProvider;
    this.nutritionRepositoryProvider = nutritionRepositoryProvider;
    this.errorHandlerProvider = errorHandlerProvider;
  }

  @Override
  public ReceitasViewModel get() {
    return newInstance(receitasRepositoryProvider.get(), nutritionRepositoryProvider.get(), errorHandlerProvider.get());
  }

  public static ReceitasViewModel_Factory create(
      Provider<ReceitasRepository> receitasRepositoryProvider,
      Provider<NutritionRepository> nutritionRepositoryProvider,
      Provider<ErrorHandler> errorHandlerProvider) {
    return new ReceitasViewModel_Factory(receitasRepositoryProvider, nutritionRepositoryProvider, errorHandlerProvider);
  }

  public static ReceitasViewModel newInstance(ReceitasRepository receitasRepository,
      NutritionRepository nutritionRepository, ErrorHandler errorHandler) {
    return new ReceitasViewModel(receitasRepository, nutritionRepository, errorHandler);
  }
}
