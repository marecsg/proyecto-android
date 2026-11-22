package com.pe.domain.usecase.Notificaciones

import com.pe.domain.repository.FcmTokenRepository

class RegistrarFcmTokenUseCase(
    private val repository: FcmTokenRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        return repository.registrar(token)
    }
}
