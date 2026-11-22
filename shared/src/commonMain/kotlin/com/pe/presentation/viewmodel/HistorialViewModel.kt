
package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.pe.domain.model.Historial
import com.pe.domain.model.Usuario
import com.pe.domain.usecase.Historial.ObtenerHistorialEstudianteUseCase
import com.pe.domain.usecase.Historial.ObtenerHistorialEncargadoUseCase

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados que podrá mostrar la pantalla
sealed interface HistorialUiState {

    data object Idle : HistorialUiState

    data object Loading : HistorialUiState

    data class Success(
        val historial: List<Historial>
    ) : HistorialUiState

    data object Empty : HistorialUiState

    data class Error(
        val mensaje: String
    ) : HistorialUiState
}

class HistorialViewModel(
    private val obtenerHistorialEstudiante:
    ObtenerHistorialEstudianteUseCase,

    private val obtenerHistorialEncargado:
    ObtenerHistorialEncargadoUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<HistorialUiState>(
            HistorialUiState.Idle
        )

    val uiState: StateFlow<HistorialUiState> =
        _uiState.asStateFlow()

    private var cargaActual: Job? = null

    // Cargar historial según el usuario autenticado
    fun cargarHistorial(usuario: Usuario) {

        // Evitar que una consulta anterior
        // sobrescriba los resultados nuevos
        cargaActual?.cancel()

        _uiState.value = HistorialUiState.Loading

        cargaActual = viewModelScope.launch {

            // Seleccionar el UseCase según el rol
            val resultado = if (usuario.esEncargado) {

                obtenerHistorialEncargado(
                    usuario.idUsuario
                )

            } else {

                obtenerHistorialEstudiante(
                    usuario.idUsuario
                )
            }

            resultado
                .onSuccess { lista ->

                    _uiState.value = if (lista.isEmpty()) {

                        HistorialUiState.Empty

                    } else {

                        HistorialUiState.Success(lista)
                    }
                }
                .onFailure { error ->

                    _uiState.value = HistorialUiState.Error(
                        error.message
                            ?: "No se pudo cargar el historial"
                    )
                }
        }
    }

    // Limpiar los datos al cerrar sesión
    fun limpiar() {
        cargaActual?.cancel()
        cargaActual = null
        _uiState.value = HistorialUiState.Idle
    }
}
