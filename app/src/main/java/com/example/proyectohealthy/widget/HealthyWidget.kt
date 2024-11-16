package com.example.proyectohealthy.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.example.proyectohealthy.data.repository.RegistroDiarioRepository
import com.example.proyectohealthy.ui.viewmodel.AuthViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HealthyWidgetEntryPoint {
    fun registroDiarioRepository(): RegistroDiarioRepository
    fun perfilRepository(): PerfilRepository
    fun widgetUpdateManager(): WidgetUpdateManager
}

class HealthyWidget : GlanceAppWidgetReceiver() {

    private fun getEntryPoint(context: Context): HealthyWidgetEntryPoint {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            HealthyWidgetEntryPoint::class.java
        )
        return hiltEntryPoint
    }

    override val glanceAppWidget: GlanceAppWidget
        get() = object : GlanceAppWidget() {
            @RequiresApi(Build.VERSION_CODES.O)
            override suspend fun provideGlance(context: Context, id: GlanceId) {
                HealthyWidgetContent(
                    registroDiarioRepository = getEntryPoint(context).registroDiarioRepository(),
                    perfilRepository = getEntryPoint(context).perfilRepository(),
                    auth = FirebaseAuth.getInstance()
                ).provideGlance(context, id)
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "HealthyApp::WidgetReceiver"
            )
            wakeLock.acquire(10000)

            try {
                GlobalScope.launch(Dispatchers.Main) {
                    getEntryPoint(context).widgetUpdateManager().forceImmediateUpdate()
                }
            } finally {
                wakeLock.release()
            }
        }
    }

    private fun setupWidgetUpdates(context: Context) {
        val request = OneTimeWorkRequestBuilder<HealthyWidgetWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "widget_update",
                ExistingWorkPolicy.REPLACE,
                request
            )
    }
}