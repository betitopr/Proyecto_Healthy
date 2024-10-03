package com.example.proyectohealthy

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ProyectoHealthyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //deleteDatabase("app_database")
        FirebaseApp.initializeApp(this)
    }
}