package com.pe.domain.usecase

import com.pe.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(correo: String, password: String) = repository.login(correo, password)
}
