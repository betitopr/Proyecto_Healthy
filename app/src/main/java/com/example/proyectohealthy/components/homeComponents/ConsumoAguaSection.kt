package com.example.proyectohealthy.components.homeComponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.proyectohealthy.R
import com.example.proyectohealthy.ui.viewmodel.ConsumoAguaViewModel
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConsumoAguaSection(viewModel: ConsumoAguaViewModel) {
    val consumoAgua by viewModel.consumoAgua.collectAsState()
    val fechaSeleccionada by viewModel.fechaSeleccionada.collectAsState()
    val pesoUsuario by viewModel.pesoUsuario.collectAsState()

    val vasosRecomendados = viewModel.calcularVasosRecomendados() // Vasos necesarios por recomendación
    val vasosMostrados = viewModel.obtenerVasosMostrados() // Vasos con los 2 adicionales
    val mlPorVaso = 300

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val vasoSize = (screenWidth / 5).coerceAtMost(50.dp)

    val vasosPorFila = (screenWidth / vasoSize).toInt()
    val filasNecesarias = (vasosMostrados + vasosPorFila - 1) / vasosPorFila

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Consumo de Agua",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Total consumido: ${(consumoAgua?.cantidad ?: 0) * mlPorVaso} ml",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(vasosPorFila),
            modifier = Modifier
                .fillMaxWidth()
                .height((filasNecesarias * (vasoSize + 8.dp))),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(vasosMostrados) { index ->
                VasoDeAgua(
                    lleno = index < (consumoAgua?.cantidad ?: 0),
                    onClick = {
                        viewModel.actualizarConsumoAgua(index + 1)
                    },
                    size = vasoSize
                )
            }
        }

        Text(
            text = "Vasos consumidos: ${consumoAgua?.cantidad ?: 0} de $vasosRecomendados recomendados",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )


        Text(
            text = "Recomendación: Se sugiere beber al menos ${vasosRecomendados * mlPorVaso} ml de agua al día basado en tu peso de ${pesoUsuario?.toInt() ?: "N/A"} kg.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun VasoDeAgua(lleno: Boolean, onClick: () -> Unit, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(if (lleno) Color(0xFF2196F3) else Color.LightGray.copy(alpha = 0.5f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = if (lleno) R.drawable.ic_vaso_lleno else R.drawable.ic_vaso_vacio),
            contentDescription = if (lleno) "Vaso lleno" else "Vaso vacío",
            tint = Color.Unspecified,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}