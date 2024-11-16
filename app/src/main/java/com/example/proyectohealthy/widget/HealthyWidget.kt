package com.example.proyectohealthy.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.example.proyectohealthy.data.repository.RegistroDiarioRepository
import com.example.proyectohealthy.ui.viewmodel.AuthViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class HealthyWidget : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var registroDiarioRepository: RegistroDiarioRepository
    @Inject lateinit var widgetUpdateManager: WidgetUpdateManager

    @Inject
    lateinit var perfilRepository: PerfilRepository

    override val glanceAppWidget: GlanceAppWidget
        @RequiresApi(Build.VERSION_CODES.O)
        get() = HealthyWidgetContent(
            registroDiarioRepository = registroDiarioRepository,
            perfilRepository = perfilRepository,
            auth = FirebaseAuth.getInstance()
        )

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        setupPeriodicUpdates(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // Cancel all widget-related work
        WorkManager.getInstance(context)
            .cancelUniqueWork("widget_periodic_update")
    }


    private fun setupPeriodicUpdates(context: Context) {
        val request = PeriodicWorkRequestBuilder<HealthyWidgetWorker>(
            0, TimeUnit.MINUTES
        )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                2, TimeUnit.MINUTES
            )
            .setInitialDelay(0, TimeUnit.SECONDS) // Asegurar primera actualización inmediata
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "widget_update",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}