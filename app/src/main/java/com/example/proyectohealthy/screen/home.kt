package com.example.proyectohealthy.screen

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
import com.example.proyectohealthy.components.IngresoAlimentoComponent
import com.example.proyectohealthy.components.RegistroComidaCard
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.ui.viewmodel.RegistroComidaViewModel
import com.example.proyectohealthy.ui.components.BusquedaAlimentosDialog
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    perfilViewModel: PerfilViewModel,
    registroComidaViewModel: RegistroComidaViewModel,
    alimentoViewModel: AlimentoViewModel
) {
    val perfilState by perfilViewModel.currentPerfil.collectAsState()
    var showBusquedaDialog by remember { mutableStateOf(false) }
    var tipoComidaSeleccionado by remember { mutableStateOf("") }

    var showIngresoAlimento by remember { mutableStateOf(false) }

    val registrosComidaDiarios by registroComidaViewModel.registrosComidaDiarios.collectAsState()


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
                .padding(16.dp)
        ) {
            item {
                // Calendario (Placeholder)
                Text(
                    text = "Calendario",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            item {
                // Gráfico circular y nutrientes
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
                        // Aquí va el gráfico circular
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
            }

            item {
                // Buscador de comida
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar comida") }
                )
            }

            item {
                Text(
                    text = "Registro de Comidas",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Botones y registros para cada tipo de comida
            listOf("Desayuno", "Almuerzo", "Cena", "Snacks").forEach { tipoComida ->
                item {
                    Text(
                        text = tipoComida,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = {
                            tipoComidaSeleccionado = tipoComida
                            showBusquedaDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Agregar $tipoComida")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                    // Mostrar los registros de comida para este tipo
                    registrosComidaDiarios[tipoComida]?.forEach { registro ->
                        registro.alimentos.forEach { (alimentoId, cantidad) ->
                            // Aquí deberías obtener el Alimento correspondiente al alimentoId
                            val alimentoDummy = Alimento(id = alimentoId, nombre = "Alimento", calorias = 100, proteinas = 10f, carbohidratos = 20f, grasas = 5f, nombrePorcion = "unidad")
                            RegistroComidaCard(
                                registroComida = registro,
                                alimento = alimentoDummy,
                                onEliminar = { registroComidaViewModel.eliminarRegistroComida(registro) }
                            )
                        }
                    }
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
    if (showBusquedaDialog) {
        BusquedaAlimentosDialog(
            onDismiss = { showBusquedaDialog = false },
            onAlimentoSelected = { alimento, cantidad ->
                registroComidaViewModel.agregarAlimento(alimento, cantidad, tipoComidaSeleccionado)
                showBusquedaDialog = false
            },
            viewModel = registroComidaViewModel
        )
    }
}