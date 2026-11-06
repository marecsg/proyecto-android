package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.domain.model.Usuario
import com.pe.domain.usecase.LoginUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.data.local.SessionManager

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val usuario: Usuario) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading

            val result = loginUseCase(email, password)

            result.onSuccess { usuario ->
                println("DEBUG_AUTH: Login Success for ${usuario.correo}")
                sessionManager.saveToken(usuario.token ?: "")
                sessionManager.saveUser(usuario)
                _state.value = AuthState.Success(usuario)
            }.onFailure { e ->
                println("DEBUG_AUTH: Login Failure for ${e.message}")
                _state.value = AuthState.Error(e.message ?: "Unknown error occurred during Login")
            }
        }
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}
