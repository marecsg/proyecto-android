
package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudDto(
    val idSolicitud: Int,
    val idEstudiante: Int,
    val nombreEstudiante: String,
    val idAmbiente: Int,
    val nombreAmbiente: String,
    val motivoSolicitud: String,
    val fechaInicioReserva: String,
    val fechaFinReserva: String,
    val fechaSolicitud: String,
    val estadoSolicitud: String,
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
    val equipos: List<SolicitudEquipoRequest>
)

@Serializable
data class SolicitudEquipoRequest(
    val idEquipo: Int
)

@Serializable
data class ActualizarEstadoSolicitudRequest(
    val estadoSolicitud: String
)
