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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.MetasNutricionales
import com.example.proyectohealthy.ui.viewmodel.ProgresoNutricional

@Composable
fun ProgresoNutricionalComponent(
    progresoNutricional: ProgresoNutricional,
    metasNutricionales: MetasNutricionales
) {
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
                strokeWidth = 8.dp,
            )
            CircularProgressIndicator(
                progress = { (progresoNutricional.calorias.toFloat() / metasNutricionales.calorias).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${progresoNutricional.calorias}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "/ ${metasNutricionales.calorias}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "kcal",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Barras de progreso para macronutrientes
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            MacronutrienteProgressBar("Proteínas", progresoNutricional.proteinas, metasNutricionales.proteinas, MaterialTheme.colorScheme.primary)
            MacronutrienteProgressBar("Grasas", progresoNutricional.grasas, metasNutricionales.grasas, MaterialTheme.colorScheme.secondary)
            MacronutrienteProgressBar("Carbohidratos", progresoNutricional.carbohidratos, metasNutricionales.carbohidratos, MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
fun MacronutrienteProgressBar(nombre: String, progreso: Int, meta: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = "$nombre: $progreso / $meta g", style = MaterialTheme.typography.bodySmall)
        Box(modifier = Modifier.fillMaxWidth().height(8.dp)) {
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