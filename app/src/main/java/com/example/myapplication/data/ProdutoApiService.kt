package com.example.myapplication.data

import com.example.myapplication.model.Produto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProdutoApiService {
    @GET("produtos")
    suspend fun getProdutos(): List<Produto>

    @POST("produtos")
    suspend fun addProduto(@Body produto: Produto): Produto
}
