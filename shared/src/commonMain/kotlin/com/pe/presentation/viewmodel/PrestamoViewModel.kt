package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.domain.model.Equipment
import com.pe.domain.model.LabSession
import com.pe.domain.model.Loan
import com.pe.domain.model.LoanDraft
import com.pe.domain.usecase.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrestamoUiState(
    val prestamos: List<Loan> = emptyList(),
    val solicitudesEncargado: List<Loan> = emptyList(),
    val ultimoCreado: Loan? = null,
    val loading: Boolean = false,
    val processing: Boolean = false,
    val errorMessage: String? = null
)

class PrestamoViewModel(
    private val obtenerEstudiante: ObtenerPrestamosEstudianteUseCase,
    private val obtenerEncargado: ObtenerPrestamosEncargadoUseCase,
    private val solicitarPrestamo: SolicitarPrestamoUseCase,
    private val responderPrestamo: ResponderPrestamoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState = _uiState.asStateFlow()

    fun loadStudent(session: LabSession) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            try {
                val values = obtenerEstudiante(session)
                _uiState.update { it.copy(prestamos = values, loading = false) }
            } catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) { _uiState.update { it.copy(loading = false, errorMessage = ex.message) } }
        }
    }

    fun loadManager(session: LabSession) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            try {
                val values = obtenerEncargado(session)
                _uiState.update { it.copy(solicitudesEncargado = values, loading = false) }
            } catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) { _uiState.update { it.copy(loading = false, errorMessage = ex.message) } }
        }
    }

    fun solicitar(session: LabSession, equipo: Equipment, draft: LoanDraft, onCreated: (Loan) -> Unit) {
        if (_uiState.value.processing) return
        viewModelScope.launch {
            _uiState.update { it.copy(processing = true, errorMessage = null) }
            try {
                val created = solicitarPrestamo(session, equipo, draft)
                _uiState.update { it.copy(ultimoCreado = created, processing = false) }
                onCreated(created)
            } catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) {
                _uiState.update { it.copy(processing = false, errorMessage = ex.message ?: "No se pudo enviar el préstamo") }
            }
        }
    }

    fun responder(session: LabSession, loan: Loan, aceptar: Boolean) {
        if (_uiState.value.processing || loan.remoteId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(processing = true, errorMessage = null) }
            try {
                responderPrestamo(session, loan.remoteId, aceptar)
                val updated = obtenerEncargado(session)
                _uiState.update { it.copy(solicitudesEncargado = updated, processing = false) }
            } catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) {
                _uiState.update { it.copy(processing = false, errorMessage = ex.message ?: "No se pudo responder") }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
    fun clear() { _uiState.value = PrestamoUiState() }
}
