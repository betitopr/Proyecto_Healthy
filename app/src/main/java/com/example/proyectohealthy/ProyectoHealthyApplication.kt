package com.example.proyectohealthy

import android.app.Application
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


@HiltAndroidApp
class ProyectoHealthyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        FirebaseDatabase.getInstance().reference.child("test").setValue("test")
            .addOnSuccessListener { Log.d("Firebase", "Write successful") }
            .addOnFailureListener { e -> Log.e("Firebase", "Write failed", e) }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}