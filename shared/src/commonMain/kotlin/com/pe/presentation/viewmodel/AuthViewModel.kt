package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.domain.model.Usuario
import com.pe.domain.usecase.Auth.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.pe.domain.usecase.Notificaciones.RegistrarFcmTokenUseCase

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val usuario: Usuario) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registrarFcmTokenUseCase: RegistrarFcmTokenUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            _state.value = AuthState.Error("Ingresa tu correo y contraseña.")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading

            loginUseCase(correo, password)
                .onSuccess { usuario ->
                    _state.value = AuthState.Success(usuario)
                }
                .onFailure { e ->
                    _state.value = AuthState.Error(
                        e.message ?: "Error desconocido al iniciar sesión"
                    )
                }
        }
    }


    fun registrarTokenFcm(token: String) {

        if (token.isBlank()) return

        // Registrar únicamente cuando el login fue exitoso
        if (_state.value !is AuthState.Success) return

        viewModelScope.launch {
            registrarFcmTokenUseCase(token)
                .onSuccess {
                    println("FCM registrado correctamente")
                }
                .onFailure { error ->
                    println("Error registrando FCM: ${error.message}")
                }
        }
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}