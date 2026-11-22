
package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudDto(
    val idSolicitud: Int,
    val codigoCorrelativo: String? = null,
    val idEstudiante: Int,
    val nombreEstudiante: String,
    val codigoEstudiante: String? = null,
    val idAmbiente: Int,
    val nombreAmbiente: String,
    val motivoSolicitud: String,
    val fechaInicioReserva: String,
    val fechaFinReserva: String,
    val fechaSolicitud: String,
    val estadoSolicitud: String,
    val fechaRespuesta: String? = null,
    val comentarioRespuesta: String? = null,
    val detalles: List<SolicitudDetalleDto> = emptyList()
)

@Serializable
data class SolicitudDetalleDto(
    val idEquipo: Int,
    val codigoQrEquipo: String,
    val nombreEquipo: String
)

@Serializable
data class CrearSolicitudRequest(
    val idEstudiante: Int,
    val idAmbiente: Int,
    val motivoSolicitud: String,
    val fechaInicioReserva: String,
    val fechaFinReserva: String,
    val equipos: List<SolicitudEquipoRequest>,
    val aceptoTerminos: Boolean = false
)

@Serializable
data class SolicitudEquipoRequest(
    val idEquipo: Int
)

@Serializable
data class ActualizarEstadoSolicitudRequest(
    val estadoSolicitud: String,
    val comentarioRespuesta: String? = null
)
