package com.example.myapplication.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.ReceitasRepository
import com.example.myapplication.data.ConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(context)
            val receitaDao = database.receitaDao()
            val connectivityObserver = ConnectivityObserver(context)
            val receitasRepository = ReceitasRepository(receitaDao, connectivityObserver)

            // Verificar se há conectividade
            if (!connectivityObserver.isConnected()) {
                return@withContext Result.retry()
            }

            // Buscar receitas não sincronizadas
            val unsyncedReceitas = receitaDao.getUnsyncedReceitas()
            
            if (unsyncedReceitas.isEmpty()) {
                return@withContext Result.success()
            }

            // Sincronizar cada receita
            for (receita in unsyncedReceitas) {
                try {
                    // Converter ReceitaEntity para Map para o Firebase
                    val receitaMap = mapOf<String, Any?>(
                        "id" to receita.id,
                        "nome" to receita.nome,
                        "descricaoCurta" to receita.descricaoCurta,
                        "imagemUrl" to receita.imagemUrl,
                        "ingredientes" to receita.ingredientes,
                        "modoPreparo" to receita.modoPreparo,
                        "tempoPreparo" to receita.tempoPreparo,
                        "porcoes" to receita.porcoes,
                        "userId" to receita.userId,
                        "userEmail" to receita.userEmail,
                        "curtidas" to receita.curtidas,
                        "favoritos" to receita.favoritos
                    )

                    // Salvar no Firebase
                    receitasRepository.salvarReceitaNoFirebase(receitaMap)
                    
                    // Marcar como sincronizada
                    receitaDao.markAsSynced(receita.id)
                    
                } catch (e: Exception) {
                    // Log do erro mas continua com as próximas receitas
                    println("Erro ao sincronizar receita ${receita.id}: ${e.message}")
                }
            }

            Result.success()
            
        } catch (e: Exception) {
            println("Erro no SyncWorker: ${e.message}")
            Result.failure()
        }
    }
} 