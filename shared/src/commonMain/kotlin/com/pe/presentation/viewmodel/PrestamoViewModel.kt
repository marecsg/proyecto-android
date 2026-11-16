    package com.pe.presentation.viewmodel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.pe.domain.model.DetallePrestamo
    import com.pe.domain.model.EstadoItem
    import com.pe.domain.model.EstadoPrestamo
    import com.pe.domain.model.Prestamo
    import com.pe.domain.model.Usuario
    import com.pe.domain.repository.PrestamoRepository.DevolucionItem
    import com.pe.domain.usecase.Prestamo.AprobarSolicitudPrestamoUseCase
    import com.pe.domain.usecase.Prestamo.CambiarEstadoPrestamoUseCase
    import com.pe.domain.usecase.Prestamo.ObtenerPrestamoPorIdUseCase
    import com.pe.domain.usecase.Prestamo.ObtenerPrestamosAmbienteUseCase
    import com.pe.domain.usecase.Prestamo.ObtenerPrestamosEstudianteUseCase
    import com.pe.domain.usecase.Prestamo.RegistrarDevolucionUseCase
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch

    data class PrestamoUiState(
        val prestamos: List<Prestamo> = emptyList(),
        val prestamoSeleccionado: Prestamo? = null,
        val loading: Boolean = false,
        val processing: Boolean = false,
        val errorMessage: String? = null,
        val successMessage: String? = null,
        val equiposSinMarcar: List<DetallePrestamo> = emptyList(),
        val mostrarAdvertenciaDevolucion: Boolean = false
    )

    class PrestamoViewModel(
        private val obtenerPorId: ObtenerPrestamoPorIdUseCase,
        private val obtenerEstudiante: ObtenerPrestamosEstudianteUseCase,
        private val obtenerAmbiente: ObtenerPrestamosAmbienteUseCase,
        private val aprobarSolicitud: AprobarSolicitudPrestamoUseCase,
        private val cambiarEstadoPrestamo: CambiarEstadoPrestamoUseCase,
        private val registrarDevolucion: RegistrarDevolucionUseCase
    ) : ViewModel() {

        private val _uiState = MutableStateFlow(PrestamoUiState())
        val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

        fun loadStudent(usuario: Usuario) {
            viewModelScope.launch {
                _uiState.update { it.copy(loading = true, errorMessage = null) }
                obtenerEstudiante(usuario.idUsuario)
                    .onSuccess { lista ->
                        _uiState.update { it.copy(prestamos = lista, loading = false) }
                    }
                    .onFailure { ex ->
                        _uiState.update {
                            it.copy(loading = false, errorMessage = ex.message ?: "Error al cargar préstamos")
                        }
                    }
            }
        }

        fun loadManager(usuario: Usuario) {
            val idAmbiente = usuario.idAmbiente
            if (idAmbiente == null) {
                _uiState.update { it.copy(errorMessage = "El encargado no tiene un ambiente asignado.") }
                return
            }

            viewModelScope.launch {
                _uiState.update { it.copy(loading = true, errorMessage = null) }
                obtenerAmbiente(idAmbiente)
                    .onSuccess { lista ->
                        _uiState.update { it.copy(prestamos = lista, loading = false) }
                    }
                    .onFailure { ex ->
                        _uiState.update {
                            it.copy(loading = false, errorMessage = ex.message ?: "Error al cargar préstamos del laboratorio")
                        }
                    }
            }
        }

        fun cargarDetalle(idPrestamo: Int) {
            viewModelScope.launch {
                _uiState.update { it.copy(loading = true, errorMessage = null) }
                obtenerPorId(idPrestamo)
                    .onSuccess { prestamo ->
                        _uiState.update { it.copy(prestamoSeleccionado = prestamo, loading = false) }
                    }
                    .onFailure { ex ->
                        _uiState.update {
                            it.copy(loading = false, errorMessage = ex.message ?: "Error al obtener el detalle del préstamo")
                        }
                    }
            }
        }

        fun aprobar(
            usuario: Usuario,
            idSolicitud: Int,
            comentarioAprobacion: String? = null,
            onApproved: (Prestamo) -> Unit = {}
        ) {
            if (_uiState.value.processing) return

            viewModelScope.launch {
                _uiState.update { it.copy(processing = true, errorMessage = null) }

                aprobarSolicitud(
                    idSolicitud = idSolicitud,
                    comentarioRespuesta  = comentarioAprobacion
                ).onSuccess { nuevoPrestamo ->
                    _uiState.update { state ->
                        state.copy(
                            prestamos = listOf(nuevoPrestamo) + state.prestamos,
                            processing = false,
                            successMessage = "Solicitud aprobada y préstamo generado"
                        )
                    }
                    onApproved(nuevoPrestamo)
                }.onFailure { ex ->
                    _uiState.update {
                        it.copy(processing = false, errorMessage = ex.message ?: "No se pudo aprobar la solicitud")
                    }
                }
            }
        }

        fun cambiarEstado(idPrestamo: Int, nuevoEstado: EstadoPrestamo) {
            if (_uiState.value.processing) return

            viewModelScope.launch {
                _uiState.update { it.copy(processing = true, errorMessage = null) }

                cambiarEstadoPrestamo(idPrestamo, nuevoEstado)
                    .onSuccess { actualizado ->
                        actualizarEnLista(actualizado, "Estado actualizado a ${nuevoEstado.name}")
                    }
                    .onFailure { ex ->
                        _uiState.update {
                            it.copy(processing = false, errorMessage = ex.message ?: "No se pudo cambiar el estado")
                        }
                    }
            }
        }

        fun intentarDevolucion(
            usuario: Usuario,
            prestamo: Prestamo,
            estadoCierre: EstadoPrestamo,
            itemsMarcados: List<DevolucionItem>
        ) {
            if (_uiState.value.processing) return

            val idsMarcados = itemsMarcados
                .filter { it.estadoItem == EstadoItem.DEVUELTO }
                .map { it.idDetalle }
                .toSet()

            val sinMarcar = prestamo.detalles.filter { it.idDetalle !in idsMarcados }

            if (sinMarcar.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        equiposSinMarcar = sinMarcar,
                        mostrarAdvertenciaDevolucion = true
                    )
                }
            } else {
                confirmarDevolucion(usuario, prestamo, estadoCierre, itemsMarcados)
            }
        }

        fun confirmarDevolucion(
            usuario: Usuario,
            prestamo: Prestamo,
            estadoCierre: EstadoPrestamo,
            itemsEvaluados: List<DevolucionItem>
        ) {
            if (_uiState.value.processing) return

            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        processing = true,
                        errorMessage = null,
                        mostrarAdvertenciaDevolucion = false,
                        equiposSinMarcar = emptyList()
                    )
                }

                registrarDevolucion(
                    prestamo = prestamo,
                    estadoCierre = estadoCierre,
                    itemsEvaluados = itemsEvaluados
                ).onSuccess { actualizado ->
                    actualizarEnLista(actualizado, "Devolución registrada con éxito")
                }.onFailure { ex ->
                    _uiState.update {
                        it.copy(processing = false, errorMessage = ex.message ?: "No se pudo registrar la devolución")
                    }
                }
            }
        }

        fun cerrarAdvertenciaDevolucion() {
            _uiState.update {
                it.copy(
                    mostrarAdvertenciaDevolucion = false,
                    equiposSinMarcar = emptyList()
                )
            }
        }

        fun clearError() { _uiState.update { it.copy(errorMessage = null) } }

        fun clearSuccess() { _uiState.update { it.copy(successMessage = null) } }
        fun clear() { _uiState.value = PrestamoUiState() }

        private fun actualizarEnLista(actualizado: Prestamo, mensaje: String) {
            _uiState.update { state ->
                state.copy(
                    prestamos = state.prestamos.map {
                        if (it.idPrestamo == actualizado.idPrestamo) actualizado else it
                    },
                    prestamoSeleccionado = if (state.prestamoSeleccionado?.idPrestamo == actualizado.idPrestamo) {
                        actualizado
                    } else {
                        state.prestamoSeleccionado
                    },
                    processing = false,
                    successMessage = mensaje
                )
            }
        }
    }