package com.example.proyectohealthy.components.homeComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.MetasNutricionales
import com.example.proyectohealthy.ui.viewmodel.ProgresoNutricional
import kotlinx.coroutines.delay

@Composable
fun ProgresoNutricionalComponent(
    progresoNutricional: ProgresoNutricional,
    metasNutricionales: MetasNutricionales
) {
    val datosCompletos = (metasNutricionales.grasas == 0 && metasNutricionales.calorias == 0)

    // Valores por defecto si no hay datos
    val metasMostradas = if (datosCompletos) {
        metasNutricionales
    } else {
        MetasNutricionales(
            calorias = 2000,
            proteinas = 150,
            carbohidratos = 200,
            grasas = 67
        )
    }

    // Asegurarnos de usar las calorías netas correctamente
    val progresoMostrado = if (datosCompletos) {
        progresoNutricional.copy(
            caloriasNetas = progresoNutricional.calorias - progresoNutricional.caloriasQuemadas
        )
    } else {
        ProgresoNutricional(
            calorias = 0,
            proteinas = 0,
            carbohidratos = 0,
            grasas = 0,
            caloriasQuemadas = 0,
            caloriasNetas = 0
        )
    }

    // Recordar el último valor válido de calorías netas
    val ultimasCaloriasNetas = remember(progresoNutricional.calorias, progresoNutricional.caloriasQuemadas) {
        progresoNutricional.calorias - progresoNutricional.caloriasQuemadas
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Gráfico circular para calorías
        Box(
            modifier = Modifier
                .size(150.dp)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = Color.LightGray,
                strokeWidth = 8.dp
            )
            CircularProgressIndicator(
                progress = { (ultimasCaloriasNetas.toFloat() / metasMostradas.calorias).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$ultimasCaloriasNetas",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "/ ${metasMostradas.calorias}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "kcal",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Mostrar desglose de calorías
                if (progresoNutricional.caloriasQuemadas > 0) {
                    Text(
                        text = "(${progresoNutricional.calorias} - ${progresoNutricional.caloriasQuemadas})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Barras de macronutrientes
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            MacronutrienteProgressBar(
                "Proteínas",
                progresoMostrado.proteinas,
                metasMostradas.proteinas,
                MaterialTheme.colorScheme.primary
            )
            MacronutrienteProgressBar(
                "Grasas",
                progresoMostrado.grasas,
                metasMostradas.grasas,
                MaterialTheme.colorScheme.secondary
            )
            MacronutrienteProgressBar(
                "Carbohidratos",
                progresoMostrado.carbohidratos,
                metasMostradas.carbohidratos,
                MaterialTheme.colorScheme.tertiary
            )
        }
    }

    // Información adicional de calorías quemadas
    if (progresoNutricional.caloriasQuemadas > 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Calorías consumidas: ${progresoNutricional.calorias}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Calorías quemadas: ${progresoNutricional.caloriasQuemadas}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }

    if (!datosCompletos) {
        Text(
            text = "Para una experiencia personalizada, complete el cuestionario de perfil",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun MacronutrienteProgressBar(
    nombre: String,
    progreso: Int,
    meta: Int,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$nombre: $progreso / $meta g",
            style = MaterialTheme.typography.bodySmall
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        ) {
            // Fondo gris para la parte vacía
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
            )
            // Barra de progreso coloreada
            Box(
                modifier = Modifier
                    .fillMaxWidth((progreso.toFloat() / meta).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}