package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.domain.model.LabSession
import com.pe.domain.usecase.LoginUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val session: LabSession? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(correo: String, password: String) {
        if (_uiState.value.loading) return
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            try {
                val session = loginUseCase(correo, password)
                _uiState.update { it.copy(session = session, loading = false) }
            } catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) {
                _uiState.update { it.copy(loading = false, errorMessage = ex.message ?: "No se pudo iniciar sesión") }
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState()
    }
}
