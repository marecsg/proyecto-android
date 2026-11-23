package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

// Respuesta que recibimos del backend
@Serializable
data class TicketIncidenciaDto(
    val idTicket: Int,
    val codigoCorrelativo: String? = null,
    val idUsuarioReporta: Int? = null,
    val nombreUsuarioReporta: String? = null,
    val idAmbiente: Int? = null,
    val nombreAmbiente: String? = null,
    val idEquipo: Int? = null,
    val nombreEquipo: String? = null,
    val tipoIncidencia: String,
    val descripcion: String,
    val estadoTicket: String,
    val fechaReporte: String? = null,
    val respuestaEncargado: String? = null,
    val fechaResolucion: String? = null,
    val nombreEncargadoAtencion: String? = null,
    val nombreEncargadoResolucion: String? = null
)

// Datos enviados al registrar una incidencia
@Serializable
data class CrearIncidenciaRequest(
    val idUsuarioReporta: Int,
    val idAmbiente: Int,
    val idEquipo: Int? = null,
    val tipoIncidencia: String,
    val descripcion: String
)

// Datos enviados al actualizar una incidencia
@Serializable
data class ResolucionIncidenciaRequest(
    val respuestaEncargado: String? = null,
    val estadoTicket: String
)