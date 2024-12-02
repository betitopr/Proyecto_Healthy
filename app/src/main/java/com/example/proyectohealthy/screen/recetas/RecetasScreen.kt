package com.example.proyectohealthy.screen.recetas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.components.CustomBottomBar
import com.example.proyectohealthy.components.CustomTopBar
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.ui.viewmodel.MisRecetasViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasScreen(
    navController: NavController,
    perfilViewModel: PerfilViewModel,
    viewModel: MisRecetasViewModel = hiltViewModel()

) {
    val perfilState by perfilViewModel.currentPerfil.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            // Resetear el estado cuando se desmonte la pantalla
            if (selectedTabIndex == 1) {
                viewModel.resetState()
            }
        }
    }

    LaunchedEffect(selectedTabIndex) {
        // Resetear el estado cuando cambie de pestaña
        if (selectedTabIndex == 0) {
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Recetas",
                userPhotoUrl = perfilState?.perfilImagen
            )
        },
        bottomBar = {
            CustomBottomBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        viewModel.resetState()  // Resetear estado al cambiar a Explorar
                    }
                ) {
                    Text(
                        text = "Explorar",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 }
                ) {
                    Text(
                        text = "Mis Recetas",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> ExplorarRecetasScreen(
                    onNavigateToMisRecetas = { selectedTabIndex = 1 }
                )
                1 -> MisRecetasScreen(
                    onNavigateToExplorar = { selectedTabIndex = 0 }
                )
            }
        }
    }
}