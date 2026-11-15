package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.domain.model.Equipment
import com.pe.domain.model.Usuario
import com.pe.domain.model.Loan
import com.pe.domain.model.LoanDraft
import com.pe.domain.usecase.*
import com.pe.domain.usecase.Prestamo.ObtenerPrestamosAmbienteUseCase
import com.pe.domain.usecase.Prestamo.ObtenerPrestamosEstudianteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class PrestamoUiState(
    val prestamos: List<Loan> = emptyList(),
    val solicitudesEncargado: List<Loan> = emptyList(),
    val ultimoCreado: Loan? = null,
    val loading: Boolean = false,
    val processing: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class PrestamoViewModel(
    private val obtenerEstudiante: ObtenerPrestamosEstudianteUseCase,
    private val obtenerEncargado: ObtenerPrestamosAmbienteUseCase,
    private val solicitarPrestamo: SolicitarPrestamoUseCase,
    private val responderPrestamo: AprobarSolicitudPrestamoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState = _uiState.asStateFlow()

    fun loadStudent(usuario: Usuario) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            val result = obtenerEstudiante(usuario)
            result.onSuccess { values ->
                _uiState.update { it.copy(prestamos = values, loading = false) }
            }.onFailure { ex ->
                _uiState.update { it.copy(loading = false, errorMessage = ex.message ?: "Error al cargar préstamos") }
            }
        }
    }

    fun loadManager(usuario: Usuario) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            val result = obtenerEncargado(usuario)
            result.onSuccess { values ->
                _uiState.update { it.copy(solicitudesEncargado = values, loading = false) }
            }.onFailure { ex ->
                _uiState.update { it.copy(loading = false, errorMessage = ex.message ?: "Error al cargar solicitudes") }
            }
        }
    }

    fun solicitar(usuario: Usuario, equipo: Equipment, draft: LoanDraft, onCreated: (Loan) -> Unit) {
        if (_uiState.value.processing) return

        val validacionResult = runCatching {
            val esEncargado = usuario.rol.equals("ENCARGADO", ignoreCase = true)
            require(!esEncargado) { "Utiliza una cuenta de estudiante para solicitar" }
            require(equipo.remoteId > 0) { "El equipo no existe en el servidor" }

            val inicio = convertirFecha(draft.date) + "T" + validarHora(draft.startTime)
            val fin = convertirFecha(draft.returnDate) + "T" + validarHora(draft.endTime)
            require(fin > inicio) { "La devolución debe ser posterior a la recogida" }

            require(draft.course.isNotBlank() && draft.teacher.isNotBlank() && draft.reason.isNotBlank()) {
                "Completa curso, docente y motivo"
            }
        }

        if (validacionResult.isFailure) {
            val mensajeError = validacionResult.exceptionOrNull()?.message ?: "Datos inválidos"
            _uiState.update { it.copy(errorMessage = mensajeError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(processing = true, errorMessage = null) }

            val result = solicitarPrestamo(usuario, equipo, draft)

            result.onSuccess { created ->
                _uiState.update {
                    it.copy(
                        ultimoCreado = created,
                        processing = false,
                        successMessage = "¡Préstamo solicitado con éxito!"
                    )
                }
                onCreated(created)
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        processing = false,
                        errorMessage = ex.message ?: "No se pudo enviar el préstamo"
                    )
                }
            }
        }
    }

    fun responder(usuario: Usuario, loan: Loan, aceptar: Boolean) {
        if (_uiState.value.processing || loan.remoteId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(processing = true, errorMessage = null) }

            val responseResult = responderPrestamo(usuario, loan.remoteId.toInt(), aceptar)

            responseResult.onSuccess {
                val resultList = obtenerEncargado(usuario)
                resultList.onSuccess { updated ->
                    _uiState.update { it.copy(solicitudesEncargado = updated, processing = false) }
                }.onFailure { ex ->
                    _uiState.update { it.copy(processing = false, errorMessage = ex.message ?: "Error al actualizar la lista") }
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(processing = false, errorMessage = ex.message ?: "No se pudo responder la solicitud")
                }
            }
        }
    }

    private fun convertirFecha(fecha: String): String {
        val partes = fecha.trim().split('/')
        require(partes.size == 3) { "Fecha inválida: utiliza dd/MM/aaaa" }
        val date = runCatching { LocalDate(partes[2].toInt(), partes[1].toInt(), partes[0].toInt()) }
            .getOrNull() ?: throw IllegalArgumentException("Fecha inválida: utiliza dd/MM/aaaa")
        return date.toString()
    }

    private fun validarHora(hora: String): String {
        require(Regex("[0-9]{2}:[0-9]{2}").matches(hora)) { "Utiliza el formato de hora HH:mm" }
        val h = hora.take(2).toInt()
        val m = hora.takeLast(2).toInt()
        require(h in 0..23 && m in 0..59) { "La hora no es válida" }
        return "$hora:00"
    }

    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
    fun clearSuccess() { _uiState.update { it.copy(successMessage = null) } }
    fun clear() { _uiState.value = PrestamoUiState() }
}