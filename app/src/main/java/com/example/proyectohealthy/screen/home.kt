package com.example.proyectohealthy.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.components.CustomBottomBar
import com.example.proyectohealthy.components.CustomTopBar
import com.example.proyectohealthy.components.DateSelector
import com.example.proyectohealthy.components.IngresoAlimentoComponent
import com.example.proyectohealthy.components.RegistroComidaCard
import com.example.proyectohealthy.components.bottomsheets.RegistroAlimentoSheet
import com.example.proyectohealthy.components.homeComponents.ConsumoAguaSection
import com.example.proyectohealthy.components.homeComponents.ProgresoNutricionalComponent
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.local.entity.RegistroComida
import com.example.proyectohealthy.data.local.entity.RegistroEjercicio
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.ui.viewmodel.RegistroComidaViewModel
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.ConsumoAguaViewModel
import com.example.proyectohealthy.ui.viewmodel.EjercicioViewModel
import com.example.proyectohealthy.ui.viewmodel.FavoritosViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel
import java.time.LocalDate
import java.util.Date
import com.example.proyectohealthy.components.bottomsheets.EjercicioBottomSheet
import com.example.proyectohealthy.data.repository.CaloriasQuemadasRepository

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    perfilViewModel: PerfilViewModel,
    registroComidaViewModel: RegistroComidaViewModel,
    alimentoViewModel: AlimentoViewModel,
    misAlimentosViewModel: MisAlimentosViewModel,
    consumoAguaViewModel: ConsumoAguaViewModel,
    ejercicioViewModel: EjercicioViewModel

) {
    val perfilState by perfilViewModel.currentPerfil.collectAsState()

    val metasNutricionales by perfilViewModel.metasNutricionales.collectAsState()
    val progresoNutricional by registroComidaViewModel.progresoNutricional.collectAsState()

    var tipoComidaSeleccionado by remember { mutableStateOf("") }
    val fechaSeleccionada by registroComidaViewModel.fechaSeleccionada.collectAsState()
    val registrosComidaDiarios by registroComidaViewModel.registrosComidaDiarios.collectAsState()
    val registrosEjercicio by ejercicioViewModel.registrosEjercicio.collectAsState()
    val ejercicios by ejercicioViewModel.ejercicios.collectAsState()
    val favoritosViewModel: FavoritosViewModel = hiltViewModel()
    val caloriasQuemadas by ejercicioViewModel.caloriasQuemadas.collectAsState()

    var showIngresoEjercicio by remember { mutableStateOf(false) }
    var showAlimentoBottomSheet by remember { mutableStateOf(false) }
    var showEjercicioBottomSheet by remember { mutableStateOf(false) }
    var showIngresoAlimento by remember { mutableStateOf(false) }

    // Efecto para sincronizar datos cuando cambia la fecha
    LaunchedEffect(fechaSeleccionada, caloriasQuemadas) {
        // Actualizaciones relacionadas con la fecha
        consumoAguaViewModel.setFechaSeleccionada(fechaSeleccionada)
        ejercicioViewModel.cargarRegistrosEjercicioPorFecha(fechaSeleccionada)

        // Cargar registros de comida y actualizar calorías quemadas en una sola operación
        registroComidaViewModel.cargarDatosCompletos(
            fecha = fechaSeleccionada,
            caloriasQuemadas = caloriasQuemadas
        )
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
                        ejercicioViewModel.setFechaSeleccionada(newDate)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                metasNutricionales?.let { metas ->
                    ProgresoNutricionalComponent(progresoNutricional, metas)
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
                EjercicioSection(
                    registros = registrosEjercicio,
                    ejercicios = ejercicios,
                    onAddClick = { showEjercicioBottomSheet = true },
                    onEliminarRegistro = { registro ->
                        ejercicioViewModel.eliminarRegistroEjercicio(registro)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Indicador de agua
            item {
                ConsumoAguaSection(viewModel = consumoAguaViewModel)
                Spacer(modifier = Modifier.height(16.dp))
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

            // Nueva sección para agregar ejercicios
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showIngresoEjercicio = !showIngresoEjercicio },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showIngresoEjercicio) "Ocultar Ingreso de Ejercicios" else "Mostrar Ingreso de Ejercicios")
                }
                if (showIngresoEjercicio) {
                    IngresoEjercicioComponent(viewModel = ejercicioViewModel)
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
        RegistroAlimentoSheet(
            onDismiss = { showAlimentoBottomSheet = false },
            onAlimentoSelected = { alimento, cantidad, tipoComida ->
                registroComidaViewModel.agregarAlimento(
                    alimento = alimento,
                    cantidad = cantidad,
                    tipoComida = tipoComida // El tipo de comida viene del selector en el BottomSheet
                )
                showAlimentoBottomSheet = false
            },
            onMiAlimentoSelected = { miAlimento, cantidad, tipoComida ->
                registroComidaViewModel.agregarMiAlimento(
                    miAlimento = miAlimento,
                    cantidad = cantidad,
                    tipoComida = tipoComida // El tipo de comida también viene del selector
                )
                showAlimentoBottomSheet = false
            },
            alimentoViewModel = alimentoViewModel,
            misAlimentosViewModel = misAlimentosViewModel,
            favoritosViewModel = favoritosViewModel,
            tipoComidaSeleccionado = tipoComidaSeleccionado // Pasar el tipo de comida seleccionado
        )
    }
    // Nuevo BottomSheet para agregar ejercicio

    if (showEjercicioBottomSheet) {
        EjercicioBottomSheet(
            ejercicios = ejercicios,
            onDismiss = { showEjercicioBottomSheet = false },
            onEjercicioSelected = { idEjercicio, duracion ->
                ejercicioViewModel.agregarRegistroEjercicio(idEjercicio, duracion)
                showEjercicioBottomSheet = false
            }
        )
    }
}

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun EjercicioSection(
    registros: List<RegistroEjercicio>,
    ejercicios: List<Ejercicio>,
    onAddClick: () -> Unit,
    onEliminarRegistro: (RegistroEjercicio) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Solo el título y botón
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ejercicios",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Lista de ejercicios registrados (sin mensaje cuando está vacía)
        registros.forEach { registro ->
            val ejercicio = ejercicios.find { it.id == registro.idEjercicio }
            if (ejercicio != null) {
                EjercicioItem(
                    registroEjercicio = registro,
                    ejercicio = ejercicio,
                    onEliminar = { onEliminarRegistro(registro) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Botón de agregar
        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Ejercicio")
        }
    }
}

@Composable
fun EjercicioItem(
    registroEjercicio: RegistroEjercicio,
    ejercicio: Ejercicio,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ejercicio.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Duración: ${registroEjercicio.duracionMinutos} minutos",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Calorías quemadas: ${registroEjercicio.duracionMinutos * ejercicio.caloriasPorMinuto}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar")
            }
        }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IngresoEjercicioComponent(viewModel: EjercicioViewModel) {
    var nombre by remember { mutableStateOf("") }
    var caloriasPorMinuto by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ingresar Nuevo Ejercicio", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del ejercicio") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = caloriasPorMinuto,
            onValueChange = { caloriasPorMinuto = it },
            label = { Text("Calorías por minuto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nombre.isNotBlank() && caloriasPorMinuto.isNotBlank()) {
                    val nuevoEjercicio = Ejercicio(
                        nombre = nombre,
                        caloriasPorMinuto = caloriasPorMinuto.toIntOrNull() ?: 0
                    )
                    viewModel.crearEjercicio(nuevoEjercicio)
                    nombre = ""
                    caloriasPorMinuto = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Agregar Ejercicio")
        }
    }
}