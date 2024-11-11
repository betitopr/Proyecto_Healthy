package com.example.proyectohealthy.widget

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.example.proyectohealthy.data.repository.RegistroDiarioRepository
import com.example.proyectohealthy.ui.viewmodel.AuthViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HealthyWidget : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var registroDiarioRepository: RegistroDiarioRepository

    @Inject
    lateinit var perfilRepository: PerfilRepository

    override val glanceAppWidget: GlanceAppWidget
        @RequiresApi(Build.VERSION_CODES.O)
        get() = HealthyWidgetContent(
            registroDiarioRepository = registroDiarioRepository,
            perfilRepository = perfilRepository,
            auth = FirebaseAuth.getInstance()
        )
}