package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.remote.OpenFoodFactsApi
import com.example.proyectohealthy.data.remote.Product
import com.example.proyectohealthy.data.repository.AlimentoScannedRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: AlimentoScannedRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    fun getProductInfo(barcode: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getProductInfo(barcode)
                _uiState.value = UiState.Success(response.product)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun saveMiAlimento(product: Product, idPerfil: String) {
        viewModelScope.launch {
            try {
                val miAlimento = MisAlimentos(
                    idPerfil = idPerfil,
                    marca = product.brands ?: "Desconocida",
                    nombre = product.product_name ?: "Desconocido",
                    categoria = product.categories?.split(',')?.firstOrNull() ?: "Sin categoría",
                    nombrePorcion = "100g",
                    pesoPorcion = 100f,
                    calorias = product.nutriments?.energy_100g?.toInt() ?: 0,
                    grasas = product.nutriments?.fat_100g ?: 0f,
                    grasasSaturadas = product.nutriments?.saturated_fat_100g ?: 0f,
                    grasasTrans = 0f,
                    sodio = (product.nutriments?.salt_100g ?: 0f) * 400f,
                    carbohidratos = product.nutriments?.carbohydrates_100g ?: 0f,
                    fibra = product.nutriments?.fiber_100g ?: 0f,
                    azucares = product.nutriments?.sugars_100g ?: 0f,
                    proteinas = product.nutriments?.proteins_100g ?: 0f,
                    codigoQr = "",
                    diaCreado = Date()
                )
                repository.saveMiAlimento(miAlimento)
                _uiState.value = UiState.Saved(miAlimento)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al guardar: ${e.message}")
            }
        }
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        data class Success(val product: Product) : UiState()
        data class Saved(val miAlimento: MisAlimentos) : UiState()
        data class Error(val message: String) : UiState()
    }
}