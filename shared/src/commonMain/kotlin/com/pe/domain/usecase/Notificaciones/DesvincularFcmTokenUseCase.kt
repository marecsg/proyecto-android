
package com.pe.domain.usecase.Notificaciones

import com.pe.domain.repository.FcmTokenRepository

class DesvincularFcmTokenUseCase(
    private val repository: FcmTokenRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        return repository.desvincular(token)
    }
}
