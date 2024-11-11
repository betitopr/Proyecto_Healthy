package com.example.proyectohealthy.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.color.ColorProvider
import androidx.glance.color.ColorProviders
import androidx.compose.ui.graphics.Color
import androidx.glance.ButtonColors
import androidx.glance.action.ActionParameters
import androidx.glance.unit.ColorProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectohealthy.MainActivity
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.local.entity.RegistroDiario
import com.example.proyectohealthy.data.repository.RegistroDiarioRepository
import com.example.proyectohealthy.ui.viewmodel.ConsumoAguaViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.data.repository.ConsumoAguaRepository
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.example.proyectohealthy.ui.viewmodel.AuthViewModel
import com.example.proyectohealthy.ui.viewmodel.MetasNutricionales
import com.example.proyectohealthy.ui.viewmodel.ProgresoNutricional
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take

import java.time.LocalDate
import javax.inject.Inject

// HealthyWidgetContent.kt
@RequiresApi(Build.VERSION_CODES.O)
class HealthyWidgetContent @Inject constructor(
    private val registroDiarioRepository: RegistroDiarioRepository,
    private val perfilRepository: PerfilRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : GlanceAppWidget() {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val currentUser = auth.currentUser
            val isAuthenticated = currentUser != null

            if (isAuthenticated && currentUser != null) {
                // Obtener perfil y calcular metas nutricionales
                val currentPerfil = perfilRepository.getPerfilFlow(currentUser.uid).take(1).firstOrNull()

                // Calcular metas nutricionales usando la misma lógica que en PerfilViewModel
                val metasNutricionales = currentPerfil?.let { perfil ->
                    val tmb = when (perfil.genero) {
                        "Masculino" -> (10 * perfil.pesoActual) + (6.25 * perfil.altura) - (5 * perfil.edad) + 5
                        "Femenino" -> (10 * perfil.pesoActual) + (6.25 * perfil.altura) - (5 * perfil.edad) - 161
                        else -> 0.0
                    }

                    val factorActividad = when (perfil.nivelActividad) {
                        "Sedentario" -> 1.2
                        "Ligeramente activo" -> 1.375
                        "Moderadamente activo" -> 1.55
                        "Muy activo" -> 1.725
                        "Extra activo" -> 1.9
                        else -> 1.2
                    }

                    var caloriasNecesarias = tmb * factorActividad

                    when (perfil.objetivo) {
                        "Perder peso" -> caloriasNecesarias -= 500
                        "Ganar peso" -> caloriasNecesarias += 500
                    }

                    val proteinas = (caloriasNecesarias * 0.3) / 4
                    val grasas = (caloriasNecesarias * 0.3) / 9
                    val carbohidratos = (caloriasNecesarias * 0.4) / 4

                    MetasNutricionales(
                        calorias = caloriasNecesarias.toInt(),
                        proteinas = proteinas.toInt(),
                        grasas = grasas.toInt(),
                        carbohidratos = carbohidratos.toInt()
                    )
                }

                // Obtener registro diario
                val registroDiario = currentPerfil?.let { perfil ->
                    registroDiarioRepository.obtenerRegistroDia(
                        idPerfil = perfil.uid,
                        fecha = LocalDate.now()
                    ).take(1).firstOrNull()
                }

                // Crear ProgresoNutricional
                val progresoNutricional = registroDiario?.let {
                    ProgresoNutricional(
                        calorias = it.caloriasConsumidas,
                        proteinas = it.proteinasConsumidas.toInt(),
                        carbohidratos = it.carbohidratosConsumidos.toInt(),
                        grasas = it.grasasConsumidas.toInt(),
                        caloriasQuemadas = it.caloriasQuemadas,
                        caloriasNetas = it.caloriasNetas
                    )
                } ?: ProgresoNutricional()

                provideContent {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(GlanceTheme.colors.background)
                            .padding(12.dp)
                    ) {
                        // Usar los mismos datos que en ProgresoNutricionalComponent
                        val datosCompletos = metasNutricionales?.calorias ?: 0 > 0

                        val metasMostradas = if (datosCompletos) {
                            metasNutricionales!!
                        } else {
                            MetasNutricionales(
                                calorias = 2000,
                                proteinas = 150,
                                carbohidratos = 200,
                                grasas = 67
                            )
                        }

                        val progresoMostrado = if (datosCompletos) {
                            progresoNutricional
                        } else {
                            ProgresoNutricional()
                        }

                        ProgresoCaloriasWidget(
                            calorias = progresoMostrado.caloriasNetas,
                            caloriasObjetivo = metasMostradas.calorias
                        )

                        Spacer(modifier = GlanceModifier.height(8.dp))

                        MacronutrientesWidget(
                            progreso = progresoMostrado,
                            metas = metasMostradas
                        )

                        Spacer(modifier = GlanceModifier.height(12.dp))

                        Button(
                            text = "Registrar Alimentos",
                            modifier = GlanceModifier.fillMaxWidth(),
                            onClick = actionStartActivity(
                                MainActivity::class.java,
                                actionParametersOf(
                                    ActionParameters.Key<String>("destination") to "alimentos"
                                )
                            )
                        )
                    }
                }
            } else {
                provideContent {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Inicia sesión para ver tu progreso nutricional",
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            provideContent {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se pudieron cargar los datos",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgresoCaloriasWidget(
    calorias: Int,
    caloriasObjetivo: Int
) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = "Calorías: $calorias / $caloriasObjetivo kcal",
            style = TextStyle(
                color = WidgetColors.TextColor,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        LinearProgressIndicator(
            progress = (calorias.toFloat() / caloriasObjetivo).coerceIn(0f, 1f),
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(8.dp),
            color = WidgetColors.ProteinaColor,
            backgroundColor = WidgetColors.ProgressBackground
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MacronutrientesWidget(
    progreso: ProgresoNutricional,
    metas: MetasNutricionales
) {
    // Convertir ProgresoNutricional a RegistroDiario para mantener la misma estructura
    val registroDiario = RegistroDiario(
        caloriasConsumidas = progreso.calorias,
        proteinasConsumidas = progreso.proteinas.toFloat(),
        carbohidratosConsumidos = progreso.carbohidratos.toFloat(),
        grasasConsumidas = progreso.grasas.toFloat(),
        caloriasQuemadas = progreso.caloriasQuemadas,
        caloriasNetas = progreso.caloriasNetas
    )

    MacronutrienteBar(
        nombre = "Proteínas",
        progreso = registroDiario.proteinasConsumidas.toInt(),
        meta = metas.proteinas.toInt(),
        color = WidgetColors.Primary
    )

    MacronutrienteBar(
        nombre = "Grasas",
        progreso = registroDiario.grasasConsumidas.toInt(),
        meta = metas.grasas.toInt(),
        color = WidgetColors.Tertiary
    )

    MacronutrienteBar(
        nombre = "Carbohidratos",
        progreso = registroDiario.carbohidratosConsumidos.toInt(),
        meta = metas.carbohidratos.toInt(),
        color = WidgetColors.Secondary
    )


}

@Composable
private fun MacronutrienteBar(
    nombre: String,
    progreso: Int,
    meta: Int,
    color: ColorProvider
) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$nombre: $progreso/$meta g",
            style = TextStyle(
                color = WidgetColors.TextColor,
                fontSize = 12.sp
            )
        )

        LinearProgressIndicator(
            progress = (progreso.toFloat() / meta).coerceIn(0f, 1f),
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(4.dp),
            color = color,
            backgroundColor = WidgetColors.ProgressBackground
        )
    }
}

object WidgetColors {
    // Usamos los colores del tema Light
    val Primary = androidx.glance.unit.ColorProvider(Color(0xFF4CAF50))    // LightColorScheme.primary
    val Secondary = androidx.glance.unit.ColorProvider(Color(0xFFFF9800))  // LightColorScheme.secondary
    val Tertiary = androidx.glance.unit.ColorProvider(Color(0xFF03A9F4))   // LightColorScheme.tertiary
    val ProteinaColor = androidx.glance.unit.ColorProvider(Color(0xFF4CAF50))
    val CaloriasColor = androidx.glance.unit.ColorProvider(Color.Yellow)
    val GrasasColor = androidx.glance.unit.ColorProvider(Color(0xFFFF9800))
    val CarbohidratosColor = androidx.glance.unit.ColorProvider(Color(0xFF03A9F4))
    val Background = androidx.glance.unit.ColorProvider(Color.White)
    val TextColor = androidx.glance.unit.ColorProvider(Color.Black)
    val ProgressBackground = androidx.glance.unit.ColorProvider(Color.LightGray)
}