package com.example.proyectohealthy

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.BackoffPolicy
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.work.Configuration
import androidx.work.WorkManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.work.*
import com.example.proyectohealthy.notification.ComidaNotificationWorker
import com.example.proyectohealthy.notification.setupNotificationAlarms
import com.example.proyectohealthy.widget.HealthyWidgetWorker


@HiltAndroidApp
class ProyectoHealthyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }



    override fun onCreate() {
        super.onCreate()
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Configurar Firebase Database
        FirebaseDatabase.getInstance().apply {
            reference.child("test").setValue("test")
                .addOnSuccessListener { Log.d("Firebase", "Write successful") }
                .addOnFailureListener { e -> Log.e("Firebase", "Write failed", e) }
        }
        setupNotificationAlarms(this)

        // Solicitar ignorar optimización de batería para actualizaciones del widget
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = packageName
                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    val intent = Intent().apply {
                        action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        data = Uri.parse("package:$packageName")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Log.e("Application", "Error configurando optimización de batería", e)
        }

        setupWidgetUpdates()
    }

    private fun setupWidgetUpdates() {
        try {
            val workManager = WorkManager.getInstance(this)

            // Limpiar trabajos anteriores
            workManager.cancelAllWork()

            // Configurar nuevo trabajo para el widget
            val updateRequest = OneTimeWorkRequestBuilder<HealthyWidgetWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()

            workManager.enqueueUniqueWork(
                "initial_widget_setup",
                ExistingWorkPolicy.REPLACE,
                updateRequest
            )
        } catch (e: Exception) {
            Log.e("Application", "Error configurando actualizaciones del widget", e)
        }
    }

    private fun setupNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Programar verificación cada hora
        val periodicWorkRequest = PeriodicWorkRequestBuilder<ComidaNotificationWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "comida_notification_worker",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }



}