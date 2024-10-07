package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NivelActividadScreen(
    perfilViewModel: PerfilViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    var selectedNivelActividad by remember { mutableStateOf(currentPerfil?.nivelActividad ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nivel de Actividad") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¿Cuál es tu nivel de actividad?", style = MaterialTheme.typography.headlineSmall)

            RadioButtonGroup(
                options = listOf("Sedentario", "Ligeramente activo", "Moderadamente activo", "Muy activo"),
                selectedOption = selectedNivelActividad,
                onOptionSelected = {
                    selectedNivelActividad = it
                    perfilViewModel.updateNivelActividad(it)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onPreviousClick) {
                    Text("Anterior")
                }
                Button(onClick = onNextClick) {
                    Text("Siguiente")
                }
            }
        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun NivelActividadScreenPreview() {
    NivelActividadScreen(
        viewModel = PerfilViewModel(FakePerfilRepository()), // Proporciona una instancia de PerfilViewModel con un repositorio falso para la vista previa
        onContinueClick = { /* Acción a realizar cuando se haga clic en "Continuar" */ }
    )
}

// Esta es una clase falsa para la vista previa. No es necesaria en tu código real.
class FakePerfilRepository : PerfilRepository {
    // Implementa los métodos necesarios para la vista previa
}*/