
package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.pe.domain.model.Incidencia
import com.pe.domain.model.EstadoTicket
import com.pe.domain.model.TipoIncidencia
import com.pe.domain.model.Usuario
import com.pe.domain.repository.IncidenciaRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IncidenciaUiState(
    val incidencias: List<Incidencia> = emptyList(),
    val incidenciaSeleccionada: Incidencia? = null,
    val ultimaCreada: Incidencia? = null,
    val loading: Boolean = false,
    val processing: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class IncidenciaViewModel(
    private val repository: IncidenciaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        IncidenciaUiState()
    )

    val uiState: StateFlow<IncidenciaUiState> =
        _uiState.asStateFlow()

    // 1. Cargar incidencias del laboratorio
    fun loadManager(usuario: Usuario) {

        if (!usuario.esEncargado) {
            _uiState.update {
                it.copy(
                    errorMessage = "Acceso solo para encargados",
                    loading = false
                )
            }
            return
        }

        val idAmbiente = usuario.idAmbiente

        if (idAmbiente == null) {
            _uiState.update {
                it.copy(
                    incidencias = emptyList(),
                    loading = false,
                    errorMessage = "No tienes un ambiente asignado"
                )
            }
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    loading = true,
                    errorMessage = null
                )
            }

            repository.obtenerPorAmbiente(idAmbiente)
                .onSuccess { lista ->
                    _uiState.update {
                        it.copy(
                            incidencias = lista.sortedByDescending {
                                    ticket -> ticket.fechaReporte.orEmpty()
                            },
                            loading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = error.message
                                ?: "No se pudieron cargar las incidencias"
                        )
                    }
                }
        }
    }


    // Cargar incidencias reportadas por el estudiante
    fun loadStudent(usuario: Usuario) {

        if (usuario.esEncargado) {
            _uiState.update {
                it.copy(
                    incidencias = emptyList(),
                    loading = false,
                    errorMessage = "Esta consulta es para estudiantes"
                )
            }
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    loading = true,
                    incidencias = emptyList(),
                    errorMessage = null
                )
            }

            repository.obtenerPorEstudiante(usuario.idUsuario)
                .onSuccess { lista ->

                    _uiState.update {
                        it.copy(
                            incidencias = lista
                                .sortedByDescending { ticket ->
                                    ticket.fechaReporte.orEmpty()
                                },
                            loading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->

                    _uiState.update {
                        it.copy(
                            loading = false,
                            incidencias = emptyList(),
                            errorMessage = error.message
                                ?: "No se pudieron cargar tus incidencias"
                        )
                    }
                }
        }
    }


    // 2. Consultar el detalle de una incidencia
    fun cargarDetalle(idTicket: Int) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    loading = true,
                    incidenciaSeleccionada = null,
                    errorMessage = null
                )
            }

            repository.obtenerPorId(idTicket)
                .onSuccess { incidencia ->
                    _uiState.update {
                        it.copy(
                            incidenciaSeleccionada = incidencia,
                            loading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = error.message
                                ?: "No se pudo cargar la incidencia"
                        )
                    }
                }
        }
    }

    // 3. Registrar una nueva incidencia
    fun registrar(
        usuario: Usuario,
        idAmbiente: Int,
        idEquipo: Int?,
        tipoIncidencia: TipoIncidencia,
        descripcion: String
    ) {

        if (_uiState.value.processing) return

        if (descripcion.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Escribe una descripción"
                )
            }
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    processing = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            repository.registrarIncidencia(
                idUsuarioReporta = usuario.idUsuario,
                idAmbiente = idAmbiente,
                idEquipo = idEquipo,
                tipoIncidencia = tipoIncidencia,
                descripcion = descripcion
            )
                .onSuccess { creada ->
                    _uiState.update {
                        it.copy(
                            ultimaCreada = creada,
                            incidenciaSeleccionada = creada,
                            processing = false,
                            successMessage =
                                "Incidencia registrada correctamente"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            processing = false,
                            errorMessage = error.message
                                ?: "No se pudo registrar la incidencia"
                        )
                    }
                }
        }
    }

    // 4. Cambiar estado o resolver incidencia
    fun cambiarEstado(
        usuario: Usuario,
        idTicket: Int,
        nuevoEstado: EstadoTicket,
        respuestaEncargado: String? = null
    ) {

        if (_uiState.value.processing) return

        if (!usuario.esEncargado) {
            _uiState.update {
                it.copy(
                    errorMessage = "Solo el encargado puede cambiar estados"
                )
            }
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    processing = true,
                    errorMessage = null,
                    successMessage = null
                )
            }

            repository.actualizarResolucion(
                idTicket = idTicket,
                nuevoEstado = nuevoEstado,
                respuestaEncargado = respuestaEncargado
            )
                .onSuccess { actualizada ->

                    _uiState.update { state ->
                        state.copy(
                            incidencias = state.incidencias.map {
                                if (it.idTicket == actualizada.idTicket) {
                                    actualizada
                                } else {
                                    it
                                }
                            },
                            incidenciaSeleccionada = actualizada,
                            processing = false,
                            successMessage =
                                "Incidencia actualizada correctamente"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            processing = false,
                            errorMessage = error.message
                                ?: "No se pudo actualizar la incidencia"
                        )
                    }
                }
        }
    }

    // 5. Limpiar mensajes
    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun clearSuccess() {
        _uiState.update {
            it.copy(successMessage = null)
        }
    }

    // 6. Limpiar información al cerrar sesión
    fun clear() {
        _uiState.value = IncidenciaUiState()
    }
}

