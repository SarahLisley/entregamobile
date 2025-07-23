package com.example.myapplication.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.model.ProdutoEntity

class ProdutoSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.produtoDao()
        val api = RetrofitInstance.api
        val pendentes = dao.getPendentes()
        var sucesso = true
        for (produto in pendentes) {
            try {
                api.addProduto(com.example.myapplication.model.Produto(
                    id = produto.id,
                    nome = produto.nome,
                    preco = produto.preco
                ))
                dao.update(produto.copy(sincronizado = true))
            } catch (e: Exception) {
                sucesso = false
            }
        }
        return if (sucesso) Result.success() else Result.retry()
    }
}
