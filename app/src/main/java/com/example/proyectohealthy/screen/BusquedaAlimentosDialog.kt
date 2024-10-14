//package com.example.proyectohealthy.ui.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.proyectohealthy.data.local.entity.Alimento
//import com.example.proyectohealthy.screen.DetalleAlimentoDialog
//import com.example.proyectohealthy.ui.viewmodel.RegistroComidaViewModel
//
//@Composable
//fun BusquedaAlimentosDialog(
//    onDismiss: () -> Unit,
//    onAlimentoSelected: (Alimento, Float) -> Unit,
//    viewModel: RegistroComidaViewModel
//) {
//    var searchQuery by remember { mutableStateOf("") }
//    val alimentosBuscados by viewModel.alimentosBuscados.collectAsState()
//    var showDetalleDialog by remember { mutableStateOf(false) }
//    var alimentoSeleccionado by remember { mutableStateOf<Alimento?>(null) }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Buscar Alimento") },
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = searchQuery,
//                    onValueChange = {
//                        searchQuery = it
//                        viewModel.buscarAlimentos(it)
//                    },
//                    label = { Text("Buscar comida") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                LazyColumn {
//                    items(alimentosBuscados) { alimento ->
//                        TextButton(onClick = {
//                            alimentoSeleccionado = alimento
//                            showDetalleDialog = true
//                        }) {
//                            Text(alimento.nombre)
//                        }
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cerrar")
//            }
//        }
//    )
//
//    // Mostrar el diálogo de detalle cuando se selecciona un alimento
//    if (showDetalleDialog && alimentoSeleccionado != null) {
//        DetalleAlimentoDialog(
//            alimento = alimentoSeleccionado!!,
//            onDismiss = { showDetalleDialog = false },
//            onConfirm = { cantidad ->
//                onAlimentoSelected(alimentoSeleccionado!!, cantidad)
//                showDetalleDialog = false
//            }
//        )
//    }
//}