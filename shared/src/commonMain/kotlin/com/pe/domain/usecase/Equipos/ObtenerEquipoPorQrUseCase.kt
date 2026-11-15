package com.pe.domain.usecase.Equipos

import com.pe.domain.model.Equipo
import com.pe.domain.repository.EquipoRepository

class ObtenerEquipoPorQrUseCase(
    private val repository: EquipoRepository
) {
    suspend operator fun invoke(codigoQr: String): Result<Equipo> {
        val qrLimpio = codigoQr.trim()
        if (qrLimpio.isBlank()) {
            return Result.failure(IllegalArgumentException("El código QR no puede estar vacío"))
        }
        return repository.obtenerEquipoPorQr(qrLimpio)
    }
}