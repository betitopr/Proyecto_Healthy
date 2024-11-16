package com.example.proyectohealthy.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.example.proyectohealthy.data.repository.RegistroDiarioRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@HiltWorker
class HealthyWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val widgetUpdateManager: WidgetUpdateManager
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = withContext(Dispatchers.Main) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "HealthyApp:WorkerWakeLock"
        )

        return@withContext try {
            wakeLock.acquire(10000) // 10 segundos máximo
            widgetUpdateManager.forceImmediateUpdate()
            Result.success()
        } catch (e: Exception) {
            Log.e("HealthyWidgetWorker", "Error updating widget", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class WidgetUpdateManager @Inject constructor(
    private val context: Context,
    private val registroDiarioRepository: RegistroDiarioRepository,
    private val perfilRepository: PerfilRepository,
    private val auth: FirebaseAuth
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val updateMutex = Mutex()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun forceImmediateUpdate() {
        updateMutex.withLock {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "HealthyApp::WidgetUpdate"
            )

            try {
                wakeLock.acquire(5000)
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(HealthyWidgetContent::class.java)

                glanceIds.forEach { glanceId ->
                    withContext(Dispatchers.Main) {
                        HealthyWidgetContent(
                            registroDiarioRepository = registroDiarioRepository,
                            perfilRepository = perfilRepository,
                            auth = auth
                        ).update(context, glanceId)
                    }
                }

                // Forzar actualización del sistema
                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.sendBroadcast(intent)

            } catch (e: Exception) {
                Log.e("WidgetManager", "Error en actualización forzada", e)
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
            }
        }
    }
}