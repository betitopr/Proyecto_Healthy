package com.example.proyectohealthy.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
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
        return@withContext try {
            // Intentar actualizar los widgets
            widgetUpdateManager.updateWidgets()
            // Si la actualización es exitosa, devolver éxito
            Result.success()
        } catch (e: Exception) {
            Log.e("HealthyWidgetWorker", "Error updating widgets", e)
            // Si hay error y tenemos menos de 3 intentos, reintentamos
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
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

    init {
        observeDataChanges()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeDataChanges() {
        scope.launch {
            auth.currentUser?.uid?.let { userId ->
                // Observe registro diario changes
                registroDiarioRepository.obtenerRegistroDia(userId, LocalDate.now())
                    .distinctUntilChanged()
                    .collect {
                        updateWidgets()
                    }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateWidgets() {
        updateMutex.withLock {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(HealthyWidgetContent::class.java)

            glanceIds.forEach { glanceId ->
                try {
                    HealthyWidgetContent(
                        registroDiarioRepository = registroDiarioRepository,
                        perfilRepository = perfilRepository,
                        auth = auth
                    ).update(context, glanceId)

                    // Force widget refresh
                    context.sendBroadcast(Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
                } catch (e: Exception) {
                    Log.e("WidgetManager", "Error updating widget", e)
                }
            }
        }
    }

    private val _updateTrigger = MutableStateFlow(0L)

    init {
        scope.launch {
            _updateTrigger
                .debounce(500) // Evita actualizaciones muy frecuentes
                .collect {
                    updateWidgets()
                }
        }
    }

    fun requestUpdate() {
        _updateTrigger.value = System.currentTimeMillis()
    }
}


