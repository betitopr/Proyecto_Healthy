package com.example.proyectohealthy.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.components.CustomBottomBar
import com.example.proyectohealthy.components.CustomTopBar
import com.example.proyectohealthy.components.DateSelector
import com.example.proyectohealthy.components.IngresoAlimentoComponent
import com.example.proyectohealthy.components.RegistroComidaCard
import com.example.proyectohealthy.components.bottomsheets.AlimentoBottomSheet
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.local.entity.RegistroComida
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.ui.viewmodel.RegistroComidaViewModel
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import java.time.LocalDate
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    perfilViewModel: PerfilViewModel,
    registroComidaViewModel: RegistroComidaViewModel,
    alimentoViewModel: AlimentoViewModel,
    misAlimentosViewModel: MisAlimentosViewModel

) {
    val perfilState by perfilViewModel.currentPerfil.collectAsState()
    var tipoComidaSeleccionado by remember { mutableStateOf("") }
    var showAlimentoBottomSheet by remember { mutableStateOf(false) }
    var showIngresoAlimento by remember { mutableStateOf(false) }
    val fechaSeleccionada by registroComidaViewModel.fechaSeleccionada.collectAsState()
    val registrosComidaDiarios by registroComidaViewModel.registrosComidaDiarios.collectAsState()

    LaunchedEffect(fechaSeleccionada) {
        registroComidaViewModel.cargarRegistrosComidaPorFecha(fechaSeleccionada)
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Home",
                userPhotoUrl = perfilState?.perfilImagen
            )
        },
        bottomBar = {
            CustomBottomBar(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                DateSelector(
                    selectedDate = fechaSeleccionada,
                    onDateSelected = { newDate ->
                        registroComidaViewModel.setFechaSeleccionada(newDate)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Gráfico circular y nutrientes (sin modificar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Gray, shape = CircleShape)
                    ) {
                        Text(
                            text = "Gráfico Circular",
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Proteínas: 50g")
                        Text(text = "Carbohidratos: 100g")
                        Text(text = "Grasas: 30g")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            listOf("Desayuno", "Almuerzo", "Cena", "Snacks").forEach { tipoComida ->
                item {
                    ComidaSection(
                        tipoComida = tipoComida,
                        registros = registrosComidaDiarios[tipoComida] ?: emptyList(),
                        onAddClick = {
                            tipoComidaSeleccionado = tipoComida
                            showAlimentoBottomSheet = true
                        },
                        alimentoViewModel = alimentoViewModel,
                        misAlimentosViewModel = misAlimentosViewModel,
                        onEliminarRegistro = { registro ->
                            registroComidaViewModel.eliminarRegistroComida(registro)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }


            // Sección de ejercicios
            item {
                Text(
                    text = "Ejercicios",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Button(
                    onClick = { /* Agregar ejercicios */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Agregar Ejercicio")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }

            // Indicador de agua
            item {
                Text(
                    text = "Consumo de Agua",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    repeat(8) {
                        Button(
                            onClick = { /* Marcar vaso */ },
                            modifier = Modifier.size(50.dp),
                            shape = CircleShape
                        ) {
                            Text(text = "${it + 1}")
                        }
                    }
                }
            }

            // Sección para agregar nuevos alimentos
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showIngresoAlimento = !showIngresoAlimento },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showIngresoAlimento) "Ocultar Ingreso de Alimentos" else "Mostrar Ingreso de Alimentos")
                }
            }

            // Formulario de ingreso de alimentos
            if (showIngresoAlimento) {
                item {
                    IngresoAlimentoComponent(viewModel = alimentoViewModel)
                }
            }
        }
    }

    // Diálogo de búsqueda de alimentos
    if (showAlimentoBottomSheet) {
        AlimentoBottomSheet(
            onDismiss = { showAlimentoBottomSheet = false },
            onAlimentoSelected = { alimento, cantidad ->
                registroComidaViewModel.agregarAlimento(alimento, cantidad, tipoComidaSeleccionado)
                showAlimentoBottomSheet = false
            },
            onMiAlimentoSelected = { miAlimento, cantidad ->
                registroComidaViewModel.agregarMiAlimento(miAlimento, cantidad, tipoComidaSeleccionado)
                showAlimentoBottomSheet = false
            },
            alimentoViewModel = alimentoViewModel,
            misAlimentosViewModel = misAlimentosViewModel,
            tipoComidaSeleccionado = tipoComidaSeleccionado
        )
    }
}

@Composable
fun ComidaSection(
    tipoComida: String,
    registros: List<RegistroComida>,
    onAddClick: () -> Unit,
    alimentoViewModel: AlimentoViewModel,
    misAlimentosViewModel: MisAlimentosViewModel,
    onEliminarRegistro: (RegistroComida) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = tipoComida, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        registros.forEach { registro ->
            registro.alimentos.forEach { (alimentoId, cantidad) ->
                RegistroComidaCardWrapper(
                    registroComida = registro,
                    alimentoId = alimentoId,
                    cantidad = cantidad,
                    alimentoViewModel = alimentoViewModel,
                    onEliminar = { onEliminarRegistro(registro) }
                )
            }
            registro.misAlimentos.forEach { (alimentoId, cantidad) ->
                MiRegistroComidaCardWrapper(
                    registroComida = registro,
                    alimentoId = alimentoId,
                    cantidad = cantidad,
                    misAlimentosViewModel = misAlimentosViewModel,
                    onEliminar = { onEliminarRegistro(registro) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Agregar $tipoComida")
        }
    }
}


@Composable
fun RegistroComidaCardWrapper(
    registroComida: RegistroComida,
    alimentoId: String,
    cantidad: Float,
    alimentoViewModel: AlimentoViewModel,
    onEliminar: () -> Unit
) {
    var alimento by remember { mutableStateOf<Alimento?>(null) }

    LaunchedEffect(alimentoId) {
        alimento = alimentoViewModel.getAlimentoById(alimentoId)
    }

    alimento?.let {
        RegistroComidaCard(
            registroComida = registroComida,
            alimento = it,
            cantidad = cantidad,
            onEliminar = onEliminar
        )
    }
}

@Composable
fun MiRegistroComidaCardWrapper(
    registroComida: RegistroComida,
    alimentoId: String,
    cantidad: Float,
    misAlimentosViewModel: MisAlimentosViewModel,
    onEliminar: () -> Unit
) {
    var miAlimento by remember { mutableStateOf<MisAlimentos?>(null) }

    LaunchedEffect(alimentoId) {
        miAlimento = misAlimentosViewModel.getMiAlimentoById(alimentoId)
    }

    miAlimento?.let {
        RegistroComidaCard(
            registroComida = registroComida,
            alimento = it,
            cantidad = cantidad,
            onEliminar = onEliminar
        )
    }
}