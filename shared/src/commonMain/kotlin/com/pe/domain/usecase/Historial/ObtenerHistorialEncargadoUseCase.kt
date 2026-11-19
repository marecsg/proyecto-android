
package com.pe.domain.usecase.Historial

import com.pe.domain.model.Historial
import com.pe.domain.repository.HistorialRepository

class ObtenerHistorialEncargadoUseCase(
    private val repository: HistorialRepository
) {

    suspend operator fun invoke(
        idEncargado: Int
    ): Result<List<Historial>> {

        // Validar que el ID sea correcto
        if (idEncargado <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "El ID del encargado no es válido"
                )
            )
        }

        // Consultar las acciones del encargado
        return repository.obtenerPorEncargado(idEncargado)
    }
}
