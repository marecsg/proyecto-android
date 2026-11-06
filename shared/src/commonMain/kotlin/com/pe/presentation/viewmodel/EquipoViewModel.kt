package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.domain.model.Equipment
import com.pe.domain.usecase.ObtenerEquiposUseCase
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
    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val result = obtenerEquipos()

            result.onSuccess { listaEquipos ->
                _uiState.update { it.copy(equipos = listaEquipos, loading = false) }
            }.onFailure { ex ->
                _uiState.update { it.copy(loading = false, errorMessage = ex.message ?: "Error al cargar equipos") }
            }
        }
    }

    fun clear() {
        _uiState.value = EquipoUiState()
    }
}