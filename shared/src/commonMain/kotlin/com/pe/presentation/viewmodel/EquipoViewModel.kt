package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.domain.model.Equipment
import com.pe.domain.model.LabSession
import com.pe.domain.usecase.ObtenerEquiposUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EquipoUiState(
    val equipos: List<Equipment> = emptyList(),
    val loading: Boolean = false,
    val errorMessage: String? = null
)

class EquipoViewModel(private val obtenerEquipos: ObtenerEquiposUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(EquipoUiState())
    val uiState = _uiState.asStateFlow()

    fun load(session: LabSession) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            try {
                val list = obtenerEquipos(session)
                _uiState.update { it.copy(equipos = list, loading = false) }
            } catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) {
                _uiState.update { it.copy(loading = false, errorMessage = ex.message) }
            }
        }
    }

    fun clear() { _uiState.value = EquipoUiState() }
}
