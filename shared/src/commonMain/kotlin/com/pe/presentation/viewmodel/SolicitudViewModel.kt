package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.pe.domain.model.EstadoSolicitud
import com.pe.domain.model.Solicitud
import com.pe.domain.model.Usuario

import com.pe.domain.usecase.Solicitud.CrearSolicitudUseCase
import com.pe.domain.usecase.Solicitud.ObtenerSolicitudPorIdUseCase
import com.pe.domain.usecase.Solicitud.ObtenerSolicitudesEstudianteUseCase
import com.pe.domain.usecase.Solicitud.ObtenerSolicitudesAmbienteUseCase
import com.pe.domain.usecase.Solicitud.CambiarEstadoSolicitudUseCase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SolicitudUiState(
    val solicitudes: List<Solicitud> = emptyList(),
    val solicitudSeleccionada: Solicitud? = null,
    val ultimaCreada: Solicitud? = null,
    val loading: Boolean = false,
    val processing: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class SolicitudViewModel(
    private val crearSolicitudUseCase: CrearSolicitudUseCase,
    private val obtenerPorId: ObtenerSolicitudPorIdUseCase,
    private val obtenerEstudiante: ObtenerSolicitudesEstudianteUseCase,
    private val obtenerAmbiente: ObtenerSolicitudesAmbienteUseCase,
    private val cambiarEstadoUseCase: CambiarEstadoSolicitudUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitudUiState())

    val uiState: StateFlow<SolicitudUiState> =
        _uiState.asStateFlow()

    // Consultar solicitudes del estudiante
    fun loadStudent(usuario: Usuario) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true, errorMessage = null)
            }

            obtenerEstudiante(usuario.idUsuario)
                .onSuccess { lista ->
                    _uiState.update {
                        it.copy(
                            solicitudes = lista,
                            loading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = error.message
                                ?: "Error al cargar solicitudes"
                        )
                    }
                }
        }
    }

    // Consultar solicitudes del laboratorio
    fun loadManager(usuario: Usuario) {
        val idAmbiente = usuario.idAmbiente

        if (idAmbiente == null) {
            _uiState.update {
                it.copy(
                    errorMessage = "No tienes un ambiente asignado"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true, errorMessage = null)
            }

            obtenerAmbiente(idAmbiente)
                .onSuccess { lista ->
                    _uiState.update {
                        it.copy(
                            solicitudes = lista,
                            loading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = error.message
                                ?: "Error al cargar solicitudes del ambiente"
                        )
                    }
                }
        }
    }

    // Consultar el detalle de una solicitud
    fun cargarDetalle(idSolicitud: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true, errorMessage = null)
            }

            obtenerPorId(idSolicitud)
                .onSuccess { solicitud ->
                    _uiState.update {
                        it.copy(
                            solicitudSeleccionada = solicitud,
                            loading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = error.message
                                ?: "No se pudo obtener la solicitud"
                        )
                    }
                }
        }
    }

    // Crear una nueva solicitud
    fun crear(
        usuario: Usuario,
        idAmbiente: Int,
        motivo: String,
        fechaInicio: String,
        fechaFin: String,
        idsEquipos: List<Int>
    ) {
        if (_uiState.value.processing) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    processing = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            crearSolicitudUseCase(
                idEstudiante = usuario.idUsuario,
                idAmbiente = idAmbiente,
                motivoSolicitud = motivo,
                fechaInicioReserva = fechaInicio,
                fechaFinReserva = fechaFin,
                idsEquipos = idsEquipos
            )
                .onSuccess { solicitud ->
                    _uiState.update {
                        it.copy(
                            solicitudes = listOf(solicitud) +
                                    it.solicitudes.filter { anterior ->
                                        anterior.idSolicitud != solicitud.idSolicitud
                                    },
                            ultimaCreada = solicitud,
                            processing = false,
                            successMessage = "Solicitud registrada correctamente"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            processing = false,
                            errorMessage = error.message
                                ?: "No se pudo registrar la solicitud"
                        )
                    }
                }
        }
    }

    // Actualizar estado de una solicitud
    fun cambiarEstado(
        idSolicitud: Int,
        nuevoEstado: EstadoSolicitud
    ) {
        if (_uiState.value.processing) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    processing = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            cambiarEstadoUseCase(
                idSolicitud,
                nuevoEstado
            )
                .onSuccess { actualizada ->
                    _uiState.update { state ->
                        state.copy(
                            solicitudes = state.solicitudes.map {
                                if (it.idSolicitud == actualizada.idSolicitud) {
                                    actualizada
                                } else {
                                    it
                                }
                            },
                            solicitudSeleccionada =
                                if (state.solicitudSeleccionada?.idSolicitud ==
                                    actualizada.idSolicitud
                                ) {
                                    actualizada
                                } else {
                                    state.solicitudSeleccionada
                                },
                            processing = false,
                            successMessage = "Estado actualizado correctamente"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            processing = false,
                            errorMessage = error.message
                                ?: "No se pudo actualizar el estado"
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun clear() {
        _uiState.value = SolicitudUiState()
    }
}
