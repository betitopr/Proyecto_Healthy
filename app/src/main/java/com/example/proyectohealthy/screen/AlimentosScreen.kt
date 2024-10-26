package com.example.proyectohealthy.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.components.CustomBottomBar
import com.example.proyectohealthy.components.CustomTopBar
import com.example.proyectohealthy.components.bottomsheets.AlimentoContent
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.RegistroComidaViewModel
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.FavoritosViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel
import com.example.proyectohealthy.ui.viewmodel.SearchViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentosScreen(
    navController: NavController,
    alimentoViewModel: AlimentoViewModel = hiltViewModel(),
    misAlimentosViewModel: MisAlimentosViewModel = hiltViewModel(),
    favoritosViewModel: FavoritosViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    registroComidaViewModel: RegistroComidaViewModel = hiltViewModel(),
    perfilViewModel: PerfilViewModel,
    ) {
    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val perfilState by perfilViewModel.currentPerfil.collectAsState()
    var showAlimentoBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Alimentos",
                userPhotoUrl = perfilState?.perfilImagen
            )
        },
        bottomBar = {
            CustomBottomBar(navController = navController)
        }
    ) { innerPadding ->
        AlimentoContent(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchViewModel.updateSearchQuery(query)
            },
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
            scannerViewModel = scannerViewModel,
            tipoComidaSeleccionado = "Desayuno", // Valor por defecto
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}