package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadesPreferencesScreen(
    perfilViewModel: PerfilViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    var selectedSistemaPeso by remember {
        mutableStateOf(currentPerfil?.unidadesPreferences?.sistemaPeso ?: "Métrico (kg)")
    }
    var selectedSistemaAltura by remember {
        mutableStateOf(currentPerfil?.unidadesPreferences?.sistemaAltura ?: "Métrico (cm)")
    }
    var selectedSistemaVolumen by remember {
        mutableStateOf(currentPerfil?.unidadesPreferences?.sistemaVolumen ?: "Métrico (ml)")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferencias de Medición") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Qué sistema de medidas prefieres usar?",
                style = MaterialTheme.typography.headlineSmall
            )

            // Sistema de Peso
            Column {
                Text(
                    text = "Sistema de Peso:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                RadioButtonGroupHorizontal(
                    options = listOf("Métrico (kg)", "Imperial (lb)"),
                    selectedOption = selectedSistemaPeso,
                    onOptionSelected = { selectedSistemaPeso = it }
                )
            }

            // Sistema de Altura
            Column {
                Text(
                    text = "Sistema de Altura:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                RadioButtonGroupHorizontal(
                    options = listOf("Métrico (cm)", "Imperial (ft/in)"),
                    selectedOption = selectedSistemaAltura,
                    onOptionSelected = { selectedSistemaAltura = it }
                )
            }

            // Sistema de Volumen
            Column {
                Text(
                    text = "Sistema de Volumen:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                RadioButtonGroupHorizontal(
                    options = listOf("Métrico (ml)", "Imperial (fl oz)"),
                    selectedOption = selectedSistemaVolumen,
                    onOptionSelected = { selectedSistemaVolumen = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onPreviousClick) {
                    Text("Anterior")
                }
                Button(
                    onClick = {
                        perfilViewModel.updateUnidadesPreferencias(
                            sistemaPeso = selectedSistemaPeso,
                            sistemaAltura = selectedSistemaAltura,
                            sistemaVolumen = selectedSistemaVolumen
                        )
                        onNextClick()
                    }
                ) {
                    Text("Siguiente")
                }
            }
        }
    }
}

@Composable
fun RadioButtonGroupHorizontal(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, // Espaciado horizontal entre opciones
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
    ) {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onOptionSelected(option) }
                    .padding(horizontal = 16.dp) // Espacio horizontal alrededor de cada opción
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
