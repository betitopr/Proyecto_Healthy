package com.example.proyectohealthy.components.bottomsheets


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.FavoritosViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel

@Composable
fun AlimentoTabContent(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onAlimentoSelected: (Alimento) -> Unit,
    onMiAlimentoSelected: (MisAlimentos) -> Unit,
    onAddMiAlimento: () -> Unit,
    alimentoViewModel: AlimentoViewModel,
    misAlimentosViewModel: MisAlimentosViewModel,
    favoritosViewModel: FavoritosViewModel,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Tabs
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) }
            ) {
                Text("Alimentos")
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) }
            ) {
                Text("Mis Alimentos")
            }
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { onTabSelected(2) }
            ) {
                Text("Favoritos")
            }
        }

        // Contenido de la pestaña seleccionada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedTabIndex) {
                0 -> BusquedaAlimentoTab(
                    viewModel = alimentoViewModel,
                    favoritosViewModel = favoritosViewModel,
                    onAlimentoSelected = onAlimentoSelected,
                    currentQuery = searchQuery
                )
                1 -> MisAlimentosTab(
                    viewModel = misAlimentosViewModel,
                    favoritosViewModel = favoritosViewModel,
                    onMiAlimentoSelected = onMiAlimentoSelected,
                    onAddMiAlimentoClick = onAddMiAlimento,
                    currentQuery = searchQuery
                )
                2 -> FavoritosTab(
                    viewModel = favoritosViewModel,
                    onAlimentoSelected = onAlimentoSelected,
                    onMiAlimentoSelected = onMiAlimentoSelected,
                    currentQuery = searchQuery
                )
            }
        }
    }
}