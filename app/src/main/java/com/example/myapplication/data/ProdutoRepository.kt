package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.model.ProdutoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProdutoRepository(
    private val context: Context,
    private val dao: ProdutoDao,
    private val api: ProdutoApiService
) {
    fun addProduto(nome: String, preco: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val produto = ProdutoEntity(nome = nome, preco = preco, sincronizado = false)
            dao.insert(produto)
            tentarSincronizar()
        }
    }

    fun tentarSincronizar() {
        CoroutineScope(Dispatchers.IO).launch {
            if (ConnectivityObserver(context).isConnected()) {
                val pendentes = dao.getPendentes()
                for (produto in pendentes) {
                    try {
                        api.addProduto(com.example.myapplication.model.Produto(
                            id = produto.id,
                            nome = produto.nome,
                            preco = produto.preco
                        ))
                        dao.update(produto.copy(sincronizado = true))
                    } catch (_: Exception) {
                        // Falha, tenta de novo depois
                    }
                }
            }
        }
    }
}
