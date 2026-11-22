
package com.pe.domain.usecase.Historial

import com.pe.domain.model.Historial
import com.pe.domain.repository.HistorialRepository

class ObtenerHistorialEstudianteUseCase(
    private val repository: HistorialRepository
) {

    suspend operator fun invoke(
        idEstudiante: Int
    ): Result<List<Historial>> {

        // Validamos que el ID sea válido
        if (idEstudiante <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "El ID del estudiante no es válido"
                )
            )
        }

        // Consultamos el historial real
        return repository.obtenerPorEstudiante(idEstudiante)
    }
}
