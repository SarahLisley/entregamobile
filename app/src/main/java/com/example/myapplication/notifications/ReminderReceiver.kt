// app/src/main/java/com/example/myapplication/notifications/ReminderReceiver.kt
package com.example.myapplication.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID     = "reminder_channel"
        const val EXTRA_ITEM_ID  = "itemId"
        const val EXTRA_TITLE    = "title"
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getIntExtra(EXTRA_ITEM_ID, 0)
        val title  = intent.getStringExtra(EXTRA_TITLE) ?: "Lembrete"

        // Constrói a notificação
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminder)            // use seu ícone em drawable
            .setContentTitle("Lembrete: $title")              // título
            .setContentText("Hora de verificar o item #$itemId")
            .setPriority(NotificationCompat.PRIORITY_HIGH)    // prioridade alta
            .setAutoCancel(true)                              // fecha ao clicar
        // Dispara
        NotificationManagerCompat.from(context)
            .notify(itemId, builder.build())
    }
}
