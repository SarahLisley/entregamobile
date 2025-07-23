package com.example.myapplication.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitInstance {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: ProdutoApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://sua-api.com/api/") // Troque pela URL da sua API
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ProdutoApiService::class.java)
    }
}
