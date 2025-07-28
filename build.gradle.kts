                                                                                                                                                                                                                                                                    // Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Use os alias do seu libs.versions.toml para definir os plugins para o projeto inteiro.
    // 'apply false' torna o plugin disponível para os submódulos, mas não o aplica ao projeto raiz.

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.google.services) apply false
}