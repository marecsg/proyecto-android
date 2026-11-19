package com.pe.domain.repository

import com.pe.domain.model.Incidencia
import com.pe.domain.model.EstadoTicket
import com.pe.domain.model.TipoIncidencia

interface IncidenciaRepository {

    // 1. Consultar una incidencia por ID
    suspend fun obtenerPorId(
        idTicket: Int
    ): Result<Incidencia>

    // 2. Consultar incidencias de un ambiente
    suspend fun obtenerPorAmbiente(
        idAmbiente: Int
    ): Result<List<Incidencia>>

    // 3. Registrar una nueva incidencia
    suspend fun registrarIncidencia(
        idUsuarioReporta: Int,
        idAmbiente: Int,
        idEquipo: Int?,
        tipoIncidencia: TipoIncidencia,
        descripcion: String
    ): Result<Incidencia>

    // 4. Actualizar estado y respuesta
    suspend fun actualizarResolucion(
        idTicket: Int,
        nuevoEstado: EstadoTicket,
        respuestaEncargado: String? = null
    ): Result<Incidencia>
}