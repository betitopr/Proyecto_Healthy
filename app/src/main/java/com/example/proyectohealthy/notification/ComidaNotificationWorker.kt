package com.example.proyectohealthy.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.proyectohealthy.MainActivity
import com.example.proyectohealthy.R
import com.example.proyectohealthy.data.repository.RegistroComidaRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime

@HiltWorker
class ComidaNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val registroComidaRepository: RegistroComidaRepository,
    private val auth: FirebaseAuth
) : CoroutineWorker(context, workerParams) {

    private val CHANNEL_ID = "recordatorio_comidas"
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        try {
            val currentUser = auth.currentUser?.uid ?: return Result.failure()
            val currentDate = LocalDate.now()

            // Obtener registros de comida del día
            val registros = registroComidaRepository.getRegistrosComidaPorFecha(
                currentUser,
                currentDate
            ).first()

            val hour = LocalTime.now().hour

            when {
                // Verificación de la mañana (11 AM)
                hour == 10 && !registros.any { it.tipoComida == "Desayuno" } -> {
                    showNotification(
                        1,
                        "¡No olvides tu desayuno!",
                        "El desayuno es la comida más importante del día. Registra lo que comiste para mantener un seguimiento adecuado."
                    )
                }

                // Verificación de la noche (11 PM)
                hour == 22 && registros.isEmpty() -> {
                    showNotification(
                        2,
                        "No has registrado comidas hoy",
                        "Registrar tus comidas es importante para alcanzar tus objetivos. ¡No olvides hacerlo!"
                    )
                }
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("ComidaNotificationWorker", "Error: ${e.message}")
            return Result.retry()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorio de Comidas"
            val descriptionText = "Canal para recordatorios de registro de comidas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(id: Int, title: String, content: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
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