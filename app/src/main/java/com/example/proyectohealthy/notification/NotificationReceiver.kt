package com.example.proyectohealthy.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.proyectohealthy.MainActivity
import com.example.proyectohealthy.R
import com.example.proyectohealthy.data.repository.RegistroComidaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class NotificationReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        when (intent.action) {
            "MORNING_CHECK" -> {
                checkAndNotifyMorning(context, notificationManager)
            }
            "NIGHT_CHECK" -> {
                checkAndNotifyNight(context, notificationManager)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndNotifyMorning(context: Context, notificationManager: NotificationManager) {
        GlobalScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val registros = RegistroComidaRepository(FirebaseDatabase.getInstance())
                    .getRegistrosComidaPorFecha(currentUser, LocalDate.now())
                    .first()

                if (!registros.any { it.tipoComida == "Desayuno" }) {
                    showNotification(
                        context,
                        notificationManager,
                        1,
                        "¡No olvides tu desayuno!",
                        "El desayuno es la comida más importante del día. Registra lo que comiste para mantener un seguimiento adecuado."
                    )
                }
            } catch (e: Exception) {
                Log.e("NotificationReceiver", "Error checking morning meals: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndNotifyNight(context: Context, notificationManager: NotificationManager) {
        GlobalScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val registros = RegistroComidaRepository(FirebaseDatabase.getInstance())
                    .getRegistrosComidaPorFecha(currentUser, LocalDate.now())
                    .first()

                if (registros.isEmpty()) {
                    showNotification(
                        context,
                        notificationManager,
                        2,
                        "No has registrado comidas hoy",
                        "Registrar tus comidas es importante para alcanzar tus objetivos. ¡No olvides hacerlo!"
                    )
                }
            } catch (e: Exception) {
                Log.e("NotificationReceiver", "Error checking night meals: ${e.message}")
            }
        }
    }

    private fun showNotification(
        context: Context,
        notificationManager: NotificationManager,
        id: Int,
        title: String,
        content: String
    ) {
        val channelId = "recordatorio_comidas"

        // Crear canal de notificación si no existe
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorio de Comidas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para recordatorios de registro de comidas"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Construir notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id, notification)
    }
}