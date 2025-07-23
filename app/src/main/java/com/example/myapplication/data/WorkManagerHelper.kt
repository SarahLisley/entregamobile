package com.example.myapplication.data

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    fun scheduleProdutoSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<ProdutoSyncWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "produto_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
