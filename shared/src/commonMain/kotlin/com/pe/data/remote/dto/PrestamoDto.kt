
package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PrestamoDto(
    val idPrestamo: Int,
    val idSolicitud: Int,
    val nombreEstudiante: String? = null,
    val codigoEstudiante: String? = null,
    val nombreAmbiente: String? = null,
    val idEncargadoAprobacion: Int? = null,
    val nombreEncargadoAprobacion: String? = null,
    val idEncargadoDevolucion: Int? = null,
    val nombreEncargadoDevolucion: String? = null,
    val fechaInicioUso: String? = null,
    val fechaFinUso: String? = null,
    val estadoPrestamo: String? = null,
    val detalles: List<DetallePrestamoDto> = emptyList()
)

@Serializable
data class DetallePrestamoDto(
    val idDetalle: Int,
    val idEquipo: Int,
    val codigoQrEquipo: String? = null,
    val nombreEquipo: String? = null,
    val estadoItem: String? = null,
    val estadoFisicoAlDevolver: String? = null,
    val comentarioDevolucion: String? = null
)

@Serializable
data class CrearPrestamoRequest(
    val idSolicitud: Int,
    val comentarioRespuesta: String? = null
)

@Serializable
data class ActualizarEstadoPrestamoRequest(
    val estadoPrestamo: String,
    val detallesDevolucion: List<DetalleDevolucionRequest>? = null
)

@Serializable
data class DetalleDevolucionRequest(
    val idDetalle: Int,
    val estadoItem: String,
    val estadoFisicoAlDevolver: String? = null,
    val comentarioDevolucion: String? = null
)
