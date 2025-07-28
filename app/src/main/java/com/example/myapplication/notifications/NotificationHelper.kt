// app/src/main/java/com/example/myapplication/notifications/NotificationHelper.kt
package com.example.myapplication.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object NotificationHelper {

  /**
   * Verifica se o app tem permissão para agendar alarmas exatos
   */
  fun hasExactAlarmPermission(context: Context): Boolean {
    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      alarmManager.canScheduleExactAlarms()
    } else {
      true // Para versões anteriores ao Android 12, não é necessária permissão especial
    }
    println("DEBUG: Permissão de alarme exato: $hasPermission")
    return hasPermission
  }

  /**
   * Agenda um alarme que disparará o ReminderReceiver no momento especificado.
   *
   * @param context Contexto da aplicação
   * @param itemId  ID único do item (uso como requestCode e notificationId)
   * @param title   Título exibido na notificação
   * @param timeInMillis Horário em milissegundos para disparo do alarme
   * @return true se o alarme foi agendado com sucesso, false caso contrário
   */
  fun scheduleReminder(
    context: Context,
    itemId: String,
    title: String,
    timeInMillis: Long
  ): Boolean {
    println("DEBUG: Tentando agendar lembrete para: $title")
    println("DEBUG: ItemId: $itemId")
    println("DEBUG: Tempo: $timeInMillis")
    
    return try {
      // Verifica se tem permissão para alarmes exatos
      if (!hasExactAlarmPermission(context)) {
        println("DEBUG: Sem permissão para alarmes exatos")
        return false
      }

      val itemIdInt = itemId.hashCode()
      val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("itemId", itemIdInt)
        putExtra("title", title)
      }

      val pendingIntent = PendingIntent.getBroadcast(
        context,
        itemIdInt,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          timeInMillis,
          pendingIntent
        )
      } else {
        alarmManager.setExact(
          AlarmManager.RTC_WAKEUP,
          timeInMillis,
          pendingIntent
        )
      }
      
      println("DEBUG: Lembrete agendado com sucesso!")
      true
    } catch (e: SecurityException) {
      // Log do erro para debug
      println("DEBUG: Erro de segurança ao agendar lembrete: ${e.message}")
      e.printStackTrace()
      false
    } catch (e: Exception) {
      // Log de outros erros
      println("DEBUG: Erro ao agendar lembrete: ${e.message}")
      e.printStackTrace()
      false
    }
  }

  /**
   * Cancela um alarme agendado
   */
  fun cancelReminder(context: Context, itemId: String) {
    try {
      val itemIdInt = itemId.hashCode()
      val intent = Intent(context, ReminderReceiver::class.java)
      val pendingIntent = PendingIntent.getBroadcast(
        context,
        itemIdInt,
        intent,
        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
      )
      
      if (pendingIntent != null) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
