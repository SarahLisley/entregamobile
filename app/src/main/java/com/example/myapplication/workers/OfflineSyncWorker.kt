package com.example.myapplication.workers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.core.data.database.AppDatabase
import com.example.myapplication.core.data.repository.ReceitasRepository
import com.example.myapplication.core.data.network.ConnectivityObserver
import com.example.myapplication.core.data.repository.NutritionRepository
import com.example.myapplication.core.data.network.GeminiServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.myapplication.BuildConfig

/**
 * Worker de sincronização offline inteligente que adapta a estratégia
 * baseada no tipo de conexão disponível
 */
class OfflineSyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    enum class NetworkType {
        WIFI, CELLULAR, NONE
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val networkType = getNetworkType()
            Log.d("OfflineSyncWorker", "Tipo de rede detectado: $networkType")

            when (networkType) {
                NetworkType.WIFI -> {
                    Log.d("OfflineSyncWorker", "Executando sincronização completa via WiFi")
                    syncFull()
                }
                NetworkType.CELLULAR -> {
                    Log.d("OfflineSyncWorker", "Executando sincronização delta via dados móveis")
                    syncDelta()
                }
                NetworkType.NONE -> {
                    Log.d("OfflineSyncWorker", "Sem conectividade, aguardando retry")
                    return@withContext Result.retry()
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("OfflineSyncWorker", "Erro na sincronização: ${e.message}")
            Result.retry()
        }
    }

    /**
     * Detecta o tipo de rede disponível
     */
    private fun getNetworkType(): NetworkType {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            else -> NetworkType.NONE
        }
    }

    /**
     * Sincronização completa - para WiFi
     * Sincroniza todos os dados pendentes
     */
    private suspend fun syncFull() {
        val database = AppDatabase.getDatabase(context)
        val receitaDao = database.receitaDao()
        val connectivityObserver = ConnectivityObserver(context)
        val imageStorageService = com.example.myapplication.core.data.storage.ImageStorageService()
        val errorHandler = com.example.myapplication.core.ui.error.ErrorHandler()
        val receitasRepository = ReceitasRepository(
            receitaDao,
            database.nutritionDataDao(),
            connectivityObserver,
            imageStorageService,
            errorHandler
        )
        val nutritionRepository = NutritionRepository(context, GeminiServiceImpl(BuildConfig.GEMINI_API_KEY))

        // Limpar cache antigo de nutrição
        try {
            nutritionRepository.cleanOldCache()
            Log.d("OfflineSyncWorker", "Cache de nutrição limpo")
        } catch (e: Exception) {
            Log.e("OfflineSyncWorker", "Erro ao limpar cache: ${e.message}")
        }

        // Buscar todas as receitas não sincronizadas
        val unsyncedReceitas = receitaDao.getUnsyncedReceitas()
        
        if (unsyncedReceitas.isEmpty()) {
            Log.d("OfflineSyncWorker", "Nenhuma receita para sincronizar")
            return
        }

        Log.d("OfflineSyncWorker", "Sincronizando ${unsyncedReceitas.size} receitas (modo completo)")

        var successCount = 0
        var errorCount = 0
        
        for (receita in unsyncedReceitas) {
            try {
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

                receitasRepository.salvarReceitaNoFirebase(receitaMap)
                receitaDao.markAsSynced(receita.id)
                successCount++
                
            } catch (e: Exception) {
                Log.e("OfflineSyncWorker", "Erro ao sincronizar receita ${receita.id}: ${e.message}")
                errorCount++
            }
        }

        Log.d("OfflineSyncWorker", "Sincronização completa concluída: $successCount sucessos, $errorCount erros")
    }

    /**
     * Sincronização delta - para dados móveis
     * Sincroniza apenas dados essenciais e pequenos
     */
    private suspend fun syncDelta() {
        val database = AppDatabase.getDatabase(context)
        val receitaDao = database.receitaDao()
        val connectivityObserver = ConnectivityObserver(context)
        val imageStorageService = com.example.myapplication.core.data.storage.ImageStorageService()
        val errorHandler = com.example.myapplication.core.ui.error.ErrorHandler()
        val receitasRepository = ReceitasRepository(
            receitaDao,
            database.nutritionDataDao(),
            connectivityObserver,
            imageStorageService,
            errorHandler
        )

        // Buscar apenas receitas pequenas (sem imagens grandes) para sincronização delta
        val unsyncedReceitas = receitaDao.getUnsyncedReceitas()
            .filter { it.imagemUrl.isNullOrBlank() || it.imagemUrl.length < 1000 } // Filtrar por tamanho
        
        if (unsyncedReceitas.isEmpty()) {
            Log.d("OfflineSyncWorker", "Nenhuma receita pequena para sincronização delta")
            return
        }

        Log.d("OfflineSyncWorker", "Sincronizando ${unsyncedReceitas.size} receitas (modo delta)")

        var successCount = 0
        var errorCount = 0
        
        for (receita in unsyncedReceitas.take(5)) { // Limitar a 5 por vez em dados móveis
            try {
                val receitaMap = mapOf<String, Any?>(
                    "id" to receita.id,
                    "nome" to receita.nome,
                    "descricaoCurta" to receita.descricaoCurta,
                    "ingredientes" to receita.ingredientes,
                    "modoPreparo" to receita.modoPreparo,
                    "tempoPreparo" to receita.tempoPreparo,
                    "porcoes" to receita.porcoes,
                    "userId" to receita.userId,
                    "userEmail" to receita.userEmail,
                    "curtidas" to receita.curtidas,
                    "favoritos" to receita.favoritos
                    // Não incluir imagemUrl em sincronização delta para economizar dados
                )

                receitasRepository.salvarReceitaNoFirebase(receitaMap)
                receitaDao.markAsSynced(receita.id)
                successCount++
                
            } catch (e: Exception) {
                Log.e("OfflineSyncWorker", "Erro ao sincronizar receita ${receita.id}: ${e.message}")
                errorCount++
            }
        }

        Log.d("OfflineSyncWorker", "Sincronização delta concluída: $successCount sucessos, $errorCount erros")
    }
} 