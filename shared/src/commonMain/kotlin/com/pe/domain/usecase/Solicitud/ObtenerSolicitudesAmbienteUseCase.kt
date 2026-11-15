package com.pe.domain.usecase.Solicitud

import com.pe.domain.model.Solicitud
import com.pe.domain.repository.SolicitudRepository

class ObtenerSolicitudesAmbienteUseCase(
    private val repository: SolicitudRepository
) {
    suspend operator fun invoke(
        idAmbiente: Int
    ): Result<List<Solicitud>> {

        if (idAmbiente <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "El ID del ambiente no es válido"
                )
            )
        }

        return repository.obtenerPorAmbiente(
            idAmbiente
        )
    }
}
