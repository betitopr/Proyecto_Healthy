package com.example.proyectohealthy.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

fun setupNotificationAlarms(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Configurar alarma para 11 AM
    val morningIntent = Intent(context, NotificationReceiver::class.java).apply {
        action = "MORNING_CHECK"
    }
    val morningPendingIntent = PendingIntent.getBroadcast(
        context,
        1,
        morningIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Configurar alarma para 11 PM
    val nightIntent = Intent(context, NotificationReceiver::class.java).apply {
        action = "DAY_CHECK"
    }
    val nightPendingIntent = PendingIntent.getBroadcast(
        context,
        2,
        nightIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Configurar hora para 11 AM
    val morning = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 10)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        if (before(Calendar.getInstance())) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    // Configurar hora para 11 PM
    val night = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 21)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        if (before(Calendar.getInstance())) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    // Programar alarma matutina
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        morning.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        morningPendingIntent
    )

    // Programar alarma nocturna
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        night.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        nightPendingIntent
    )
}