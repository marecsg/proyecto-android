package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.domain.model.Equipo
import com.pe.domain.model.Usuario
import com.pe.domain.usecase.Equipos.ObtenerEquipoPorIdUseCase
import com.pe.domain.usecase.Equipos.ObtenerEquipoPorQrUseCase
import com.pe.domain.usecase.Equipos.ObtenerEquiposAmbienteUseCase
import com.pe.domain.usecase.Equipos.ObtenerEquiposUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EquipoUiState(
    val equipos: List<Equipo> = emptyList(),
    val equipoSeleccionado: Equipo? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null
)

class EquipoViewModel(
    private val obtenerEquipos: ObtenerEquiposUseCase,
    private val obtenerEquipoPorId: ObtenerEquipoPorIdUseCase,
    private val obtenerEquipoPorQr: ObtenerEquipoPorQrUseCase,
    private val obtenerEquiposAmbiente: ObtenerEquiposAmbienteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EquipoUiState())
    val uiState: StateFlow<EquipoUiState> = _uiState.asStateFlow()

    fun load(usuario: Usuario) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    loading = true,
                    equipos = emptyList(),
                    errorMessage = null
                )
            }

            val resultado = if (usuario.esEncargado) {

                val idAmbiente = usuario.idAmbiente

                if (idAmbiente == null) {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = "No tienes un laboratorio asignado"
                        )
                    }
                    return@launch
                }

                obtenerEquiposAmbiente(idAmbiente)

            } else {

                obtenerEquipos()
            }

            resultado
                .onSuccess { listaEquipos ->
                    _uiState.update {
                        it.copy(
                            equipos = listaEquipos,
                            loading = false
                        )
                    }
                }
                .onFailure { ex ->
                    _uiState.update {
                        it.copy(
                            equipos = emptyList(),
                            loading = false,
                            errorMessage = ex.message
                                ?: "Error al cargar equipos"
                        )
                    }
                }
        }
    }

    fun buscarPorId(idEquipo: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            obtenerEquipoPorId(idEquipo)
                .onSuccess { equipo ->
                    _uiState.update { it.copy(equipoSeleccionado = equipo, loading = false) }
                }
                .onFailure { ex ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = ex.message ?: "No se encontró el equipo"
                        )
                    }
                }
        }
    }

    fun buscarPorQr(codigoQr: String) {
        if (codigoQr.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            obtenerEquipoPorQr(codigoQr)
                .onSuccess { equipo ->
                    _uiState.update { it.copy(equipoSeleccionado = equipo, loading = false) }
                }
                .onFailure { ex ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = ex.message ?: "Código QR no registrado"
                        )
                    }
                }
        }
    }

    fun limpiarSeleccion() {
        _uiState.update { it.copy(equipoSeleccionado = null, errorMessage = null) }
    }

    fun clear() {
        _uiState.value = EquipoUiState()
    }
}