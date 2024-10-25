package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.remote.OpenFoodFactsApi
import com.example.proyectohealthy.data.remote.Product
import com.example.proyectohealthy.data.repository.AlimentoScannedRepository
import com.example.proyectohealthy.data.repository.MisAlimentosRepository
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: AlimentoScannedRepository,
    private val misAlimentosRepository: MisAlimentosRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    fun resetState() {
        _uiState.value = UiState.Initial
    }

    override fun onCleared() {
        super.onCleared()
        resetState()
    }

    fun getProductInfo(barcode: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

                // Buscar primero en MisAlimentos
                val existingAlimento = misAlimentosRepository.getMisAlimentosFlow(userId)
                    .first()
                    .find { it.codigoQr == barcode }

                if (existingAlimento != null) {
                    _uiState.value = UiState.Success(existingAlimento)
                    return@launch
                }

                // Si no existe, obtener de la API
                val response = repository.getProductInfo(barcode)
                val product = response.product

                // Verificar si existe por nombre
                val existingByName = misAlimentosRepository.getMisAlimentosFlow(userId)
                    .first()
                    .find { it.nombre.equals(product.product_name, ignoreCase = true) }

                if (existingByName != null) {
                    _uiState.value = UiState.Success(existingByName)
                    return@launch
                }

                // Crear nuevo MiAlimento
                val miAlimento = MisAlimentos(
                    idPerfil = userId,
                    nombre = product.product_name ?: "Desconocido",
                    marca = product.brands ?: "Desconocida",
                    categoria = product.categories?.split(',')?.firstOrNull() ?: "Sin categoría",
                    nombrePorcion = "100g",
                    pesoPorcion = 100f,
                    calorias = product.nutriments?.energy_100g?.toInt() ?: 0,
                    proteinas = product.nutriments?.proteins_100g ?: 0f,
                    carbohidratos = product.nutriments?.carbohydrates_100g ?: 0f,
                    grasas = product.nutriments?.fat_100g ?: 0f,
                    grasasSaturadas = product.nutriments?.saturated_fat_100g ?: 0f,
                    grasasTrans = 0f,
                    sodio = (product.nutriments?.salt_100g ?: 0f) * 400f,
                    fibra = product.nutriments?.fiber_100g ?: 0f,
                    azucares = product.nutriments?.sugars_100g ?: 0f,
                    codigoQr = barcode,
                    diaCreado = Date()
                )

                val id = misAlimentosRepository.createOrUpdateMiAlimento(miAlimento)
                _uiState.value = UiState.Success(miAlimento.copy(id = id))
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
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
        data class Success(val miAlimento: MisAlimentos) : UiState()
        data class ExistingProduct(val miAlimento: MisAlimentos) : UiState()
        data class Error(val message: String) : UiState()
        data class Saved(val miAlimento: MisAlimentos) : UiState()
    }
}