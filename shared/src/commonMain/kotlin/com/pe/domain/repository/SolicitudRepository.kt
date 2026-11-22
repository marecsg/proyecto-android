package com.pe.domain.repository

import com.pe.domain.model.Solicitud
import com.pe.domain.model.EstadoSolicitud

interface SolicitudRepository {

    suspend fun obtenerPorId(idSolicitud: Int): Result<Solicitud>

    suspend fun obtenerPorEstudiante(idEstudiante: Int): Result<List<Solicitud>>

    suspend fun obtenerPorAmbiente(idAmbiente: Int): Result<List<Solicitud>>

    suspend fun crearSolicitud(
        idEstudiante: Int,
        idAmbiente: Int,
        motivoSolicitud: String,
        fechaInicioReserva: String,
        fechaFinReserva: String,
        idsEquipos: List<Int>,
        aceptoTerminos: Boolean
    ): Result<Solicitud>

    suspend fun cambiarEstado(
        idSolicitud: Int,
        nuevoEstado: EstadoSolicitud,
        comentarioRespuesta: String? = null
    ): Result<Solicitud>
}
