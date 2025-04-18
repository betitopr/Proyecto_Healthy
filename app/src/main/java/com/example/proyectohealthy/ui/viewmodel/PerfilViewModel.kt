package com.example.proyectohealthy.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.local.entity.UnidadesPreferences
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import perfetto.protos.UiState
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val perfilRepository: PerfilRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _currentPerfil = MutableStateFlow<Perfil?>(null)
    val currentPerfil = _currentPerfil.asStateFlow()

    private val _metasNutricionales = MutableStateFlow<MetasNutricionales?>(null)
    val metasNutricionales = _metasNutricionales.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isPerfilCompleto = MutableStateFlow(false)
    val isPerfilCompleto = _isPerfilCompleto.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    init {
        observeAuthChanges()
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                perfilRepository.getPerfilFlow(user.uid).collect { perfil ->
                    _currentPerfil.value = perfil
                    calcularMetasNutricionales(perfil)
                }
            }
        }
    }

    private fun observeAuthChanges() {
        auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user ->
                loadCurrentPerfil(user.uid)
            } ?: run {
                clearCurrentPerfil()
            }
        }
    }

    private fun loadCurrentPerfil(uid: String) {
        viewModelScope.launch {
            perfilRepository.getPerfilFlow(uid).collect { perfil ->
                _currentPerfil.value = perfil ?: createDefaultPerfil(uid)
                checkPerfilCompleto()
            }
        }
    }

    fun clearCurrentPerfil() {
        _currentPerfil.value = null
        _isPerfilCompleto.value = false
        _isEditing.value = false
        _error.value = null
        Log.d("PerfilViewModel", "Perfil actual limpiado")
    }

    fun checkPerfilCompleto() {
        _currentPerfil.value?.let { perfil ->
            perfil.perfilCompleto = perfilTieneCamposCompletos(perfil)
        }
    }

    fun updateProfileImage(imageUri: Uri?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

                val newImageUrl = perfilRepository.updateProfileImage(userId, imageUri)

                // Actualizar el perfil local
                _currentPerfil.value = _currentPerfil.value?.copy(perfilImagen = newImageUrl)

                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error al actualizar la imagen: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun removeProfileImage() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

                // Eliminar imagen actual y obtener URL por defecto (si existe)
                val defaultImageUrl = perfilRepository.updateProfileImage(userId, null)

                // Actualizar perfil local
                _currentPerfil.value = _currentPerfil.value?.copy(perfilImagen = defaultImageUrl)

                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Error al eliminar la imagen: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun perfilTieneCamposCompletos(perfil: Perfil): Boolean {
        return perfil.altura > 0f &&
                perfil.pesoActual > 0f &&
                perfil.edad > 0 &&
                perfil.genero.isNotBlank() &&
                perfil.objetivo.isNotBlank() &&
                perfil.nivelActividad.isNotBlank()
    }



    private fun createDefaultPerfil(uid: String): Perfil {
        return Perfil(
            uid = uid,
            nombre = "",
            apellido = "",
            genero = "",
            altura = 0f,
            edad = 0,
            pesoActual = 0f,
            pesoObjetivo = 0f,
            nivelActividad = "",
            objetivo = "",
            comoConseguirlo = "",
            entrenamientoFuerza = "",
            perfilImagen = "",
            biografia = ""
        )
    }

    fun updatePerfil(perfil: Perfil) {
        viewModelScope.launch {
            try {
                perfilRepository.createOrUpdatePerfil(perfil)
                _currentPerfil.value = perfil
                checkPerfilCompleto()
            } catch (e: Exception) {
                _error.value = "Error al actualizar el perfil: ${e.message}"
            }
        }
    }

    fun setEditing(isEditing: Boolean) {
        _isEditing.value = isEditing
    }



    fun updateUnidadesPreferencias(
        sistemaPeso: String,
        sistemaAltura: String,
        sistemaVolumen: String
    ) = updatePerfilField { uid ->
        _currentPerfil.value?.let { currentPerfil ->
            val updatedPerfil = currentPerfil.copy(
                unidadesPreferences = UnidadesPreferences(
                    sistemaPeso = sistemaPeso,
                    sistemaAltura = sistemaAltura,
                    sistemaVolumen = sistemaVolumen
                )
            )
            perfilRepository.createOrUpdatePerfil(updatedPerfil)
        }
    }



    // Funciones de conversión de unidades
    fun convertirPeso(valor: Float, desde: String, hasta: String): Float {
        return when {
            desde == "Métrico (kg)" && hasta == "Imperial (lb)" -> valor * 2.20462f
            desde == "Imperial (lb)" && hasta == "Métrico (kg)" -> valor / 2.20462f
            else -> valor
        }
    }

    fun convertirAltura(valor: Float, desde: String, hasta: String): Float {
        return when {
            desde == "Métrico (cm)" && hasta == "Imperial (ft/in)" -> valor / 30.48f
            desde == "Imperial (ft/in)" && hasta == "Métrico (cm)" -> valor * 30.48f
            else -> valor
        }
    }

    fun convertirVolumen(valor: Float, desde: String, hasta: String): Float {
        return when {
            desde == "Métrico (ml)" && hasta == "Imperial (fl oz)" -> valor * 0.033814f
            desde == "Imperial (fl oz)" && hasta == "Métrico (ml)" -> valor / 0.033814f
            else -> valor
        }
    }

    fun updatePerfilField(updateFunction: suspend (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.currentUser?.uid?.let { uid ->
                    updateFunction(uid)
                    loadCurrentPerfil(uid)
                }
            } catch (e: Exception) {
                _error.value = "Error al actualizar el perfil: ${e.message}"
            }
        }
    }

    // Funciones de actualización específicas
    fun updateObjetivo(objetivo: String) = updatePerfilField { uid ->
        perfilRepository.updateObjetivo(uid, objetivo)
    }

    fun updateEdad(edad: Int) = updatePerfilField { uid ->
        perfilRepository.updateEdad(uid, edad)
    }

    fun updateGenero(genero: String) = updatePerfilField { uid ->
        perfilRepository.updateGenero(uid, genero)
    }

    fun updateAltura(altura: Float) = updatePerfilField { uid ->
        perfilRepository.updateAltura(uid, altura)
    }

    fun updatePesoActual(pesoActual: Float) = updatePerfilField { uid ->
        perfilRepository.updatePesoActual(uid, pesoActual)
    }

    fun updatePesoObjetivo(pesoObjetivo: Float) = updatePerfilField { uid ->
        perfilRepository.updatePesoObjetivo(uid, pesoObjetivo)
    }

    fun updateNivelActividad(nivelActividad: String) = updatePerfilField { uid ->
        perfilRepository.updateNivelActividad(uid, nivelActividad)
    }

    fun updateEntrenamientoFuerza(entrenamientoFuerza: String) = updatePerfilField { uid ->
        perfilRepository.updateEntrenamientoFuerza(uid, entrenamientoFuerza)
    }

    fun updateComoConseguirlo(comoConseguirlo: String) = updatePerfilField { uid ->
        perfilRepository.updateComoConseguirlo(uid, comoConseguirlo)
    }

    fun updatePremium(premium: Boolean) = updatePerfilField { uid ->
        perfilRepository.updatePremium(uid, premium)
    }

    fun addAlimentoFavorito(alimentoId: String) = updatePerfilField { uid ->
        perfilRepository.addAlimentoFavorito(uid, alimentoId)
    }

    fun removeAlimentoFavorito(alimentoId: String) = updatePerfilField { uid ->
        perfilRepository.removeAlimentoFavorito(uid, alimentoId)
    }

    fun addAlimentoReciente(alimentoId: String) = updatePerfilField { uid ->
        perfilRepository.addAlimentoReciente(uid, alimentoId)
    }

    private fun calcularMetasNutricionales(perfil: Perfil?) {
        perfil?.let {
            // Cambiamos la fórmula de TMB para usar la de Harris-Benedict
            val tmb = when (it.genero) {
                "Masculino" -> (10 * it.pesoActual) + (6.25 * it.altura) - (5 * it.edad) + 5
                "Femenino" -> (10 * it.pesoActual) + (6.25 * it.altura) - (5 * it.edad) - 161
                else -> 0.0
            }



            val factorActividad = when (it.nivelActividad) {
                "Sedentario" -> 1.2
                "Ligeramente activo" -> 1.375
                "Moderadamente activo" -> 1.55
                "Muy activo" -> 1.725
                "Extra activo" -> 1.9
                else -> 1.2
            }

            var caloriasNecesarias = tmb * factorActividad

            when (it.objetivo) {
                "Perder peso" -> caloriasNecesarias -= 500
                "Ganar peso" -> caloriasNecesarias += 500
            }

            val proteinas = (caloriasNecesarias * 0.3) / 4
            val grasas = (caloriasNecesarias * 0.3) / 9
            val carbohidratos = (caloriasNecesarias * 0.4) / 4

            _metasNutricionales.value = MetasNutricionales(
                calorias = caloriasNecesarias.toInt(),
                proteinas = proteinas.toInt(),
                grasas = grasas.toInt(),
                carbohidratos = carbohidratos.toInt()
            )
        }
    }

    fun clearError() {
        _error.value = null
    }
    fun signOut() {
        auth.signOut()
        clearCurrentPerfil()
    }
}

data class MetasNutricionales(
    val calorias: Int,
    val proteinas: Int,
    val grasas: Int,
    val carbohidratos: Int
)