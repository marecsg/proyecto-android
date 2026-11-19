
package com.pe.domain.repository

import com.pe.domain.model.Historial

interface HistorialRepository {

    // Historial relacionado con un estudiante
    suspend fun obtenerPorEstudiante(
        idEstudiante: Int
    ): Result<List<Historial>>

    // Historial de acciones realizadas por un encargado
    suspend fun obtenerPorEncargado(
        idEncargado: Int
    ): Result<List<Historial>>
}
