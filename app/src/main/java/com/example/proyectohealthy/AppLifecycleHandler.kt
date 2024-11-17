package com.example.proyectohealthy

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppLifecycleHandler @Inject constructor(
    @ApplicationContext private val context: Context
) : LifecycleEventObserver {
    private var lastActivityTimestamp = System.currentTimeMillis()
    private var isAppInBackground = false
    private val handler = Handler(Looper.getMainLooper())
    private val closeAppRunnable = Runnable {
        if (isAppInBackground && System.currentTimeMillis() - lastActivityTimestamp >= INACTIVITY_TIMEOUT) {
            closeApp()
        }
    }

    companion object {
        private const val INACTIVITY_TIMEOUT = 10 * 60 * 1000L // 10 minutos en milisegundos
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                isAppInBackground = true
                lastActivityTimestamp = System.currentTimeMillis()
                scheduleAppClose()
            }
            Lifecycle.Event.ON_RESUME -> {
                isAppInBackground = false
                handler.removeCallbacks(closeAppRunnable)
            }
            else -> {}
        }
    }

    private fun scheduleAppClose() {
        handler.postDelayed(closeAppRunnable, INACTIVITY_TIMEOUT)
    }

    private fun closeApp() {
        // Cerrar todas las actividades
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.appTasks.forEach { it.finishAndRemoveTask() }

        // Forzar cierre del proceso
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}