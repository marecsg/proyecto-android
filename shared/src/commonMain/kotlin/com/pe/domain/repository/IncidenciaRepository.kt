
package com.pe.domain.repository

import com.pe.domain.model.Incidencia
import com.pe.domain.model.EstadoTicket
import com.pe.domain.model.TipoIncidencia

interface IncidenciaRepository {

    // 1. Consultar incidencia por ID
    suspend fun obtenerPorId(
        idTicket: Int
    ): Result<Incidencia>

    // 2. Consultar incidencias del laboratorio
    suspend fun obtenerPorAmbiente(
        idAmbiente: Int
    ): Result<List<Incidencia>>

    // 3. NUEVO: Consultar incidencias del estudiante
    suspend fun obtenerPorEstudiante(
        idEstudiante: Int
    ): Result<List<Incidencia>>

    // 4. Registrar nueva incidencia
    suspend fun registrarIncidencia(
        idUsuarioReporta: Int,
        idAmbiente: Int,
        idEquipo: Int?,
        tipoIncidencia: TipoIncidencia,
        descripcion: String
    ): Result<Incidencia>

    // 5. Actualizar estado de incidencia
    suspend fun actualizarResolucion(
        idTicket: Int,
        nuevoEstado: EstadoTicket,
        respuestaEncargado: String? = null
    ): Result<Incidencia>
}
