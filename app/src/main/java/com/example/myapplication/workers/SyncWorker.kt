package com.example.myapplication.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.ReceitasRepository
import com.example.myapplication.data.ConnectivityObserver
import com.example.myapplication.data.NutritionRepository
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
            val nutritionRepository = NutritionRepository(context)

            // Verificar se há conectividade
            if (!connectivityObserver.isConnected()) {
                Log.d("SyncWorker", "Sem conectividade, aguardando retry")
                return@withContext Result.retry()
            }

            // Limpar cache antigo de nutrição (uma vez por dia)
            try {
                nutritionRepository.cleanOldCache()
                Log.d("SyncWorker", "Cache de nutrição limpo")
            } catch (e: Exception) {
                Log.e("SyncWorker", "Erro ao limpar cache: ${e.message}")
            }

            // Buscar receitas não sincronizadas
            val unsyncedReceitas = receitaDao.getUnsyncedReceitas()
            
            if (unsyncedReceitas.isEmpty()) {
                Log.d("SyncWorker", "Nenhuma receita para sincronizar")
                return@withContext Result.success()
            }

            Log.d("SyncWorker", "Sincronizando ${unsyncedReceitas.size} receitas")

            // Sincronizar cada receita
            var successCount = 0
            var errorCount = 0
            
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
                    successCount++
                    
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Erro ao sincronizar receita ${receita.id}: ${e.message}")
                    errorCount++
                }
            }

            Log.d("SyncWorker", "Sincronização concluída: $successCount sucessos, $errorCount erros")
            Result.success()
            
        } catch (e: Exception) {
            Log.e("SyncWorker", "Erro geral no SyncWorker: ${e.message}")
            Result.failure()
        }
    }
} 