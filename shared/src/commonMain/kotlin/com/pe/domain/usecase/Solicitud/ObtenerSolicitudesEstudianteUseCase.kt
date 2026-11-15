package com.pe.domain.usecase.Solicitud

import com.pe.domain.model.Solicitud
import com.pe.domain.repository.SolicitudRepository

class ObtenerSolicitudesEstudianteUseCase(
    private val repository: SolicitudRepository
) {
    suspend operator fun invoke(
        idEstudiante: Int
    ): Result<List<Solicitud>> {

        if (idEstudiante <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "El ID del estudiante no es válido"
                )
            )
        }

        return repository.obtenerPorEstudiante(
            idEstudiante
        )
    }
}
