// app/src/main/java/com/example/myapplication/notifications/NotificationHelper.kt
package com.example.myapplication.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object NotificationHelper {

  /**
   * Agenda um alarme que disparará o ReminderReceiver no momento especificado.
   *
   * @param context Contexto da aplicação
   * @param itemId  ID único do item (uso como requestCode e notificationId)
   * @param title   Título exibido na notificação
   * @param timeInMillis Horário em milissegundos para disparo do alarme
   */
  fun scheduleReminder(
    context: Context,
    itemId: Int,
    title: String,
    timeInMillis: Long
  ) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
      putExtra("itemId", itemId)
      putExtra("title", title)
    }

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      itemId,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      timeInMillis,
      pendingIntent
    )
  }
}
