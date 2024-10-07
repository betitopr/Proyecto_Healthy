package com.example.proyectohealthy

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ProyectoHealthyApplication : Application() {
    override fun onCreate() {
        FirebaseDatabase.getInstance().reference.child("test").setValue("test")
            .addOnSuccessListener { Log.d("Firebase", "Write successful") }
            .addOnFailureListener { e -> Log.e("Firebase", "Write failed", e) }
        super.onCreate()
        FirebaseApp.initializeApp(this)

    }
}
