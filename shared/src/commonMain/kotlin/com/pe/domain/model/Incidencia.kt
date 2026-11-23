package com.pe.domain.model

import com.pe.utils.FechaUtils.formatearFechaHora

// Estados reales del backend
enum class EstadoTicket(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En proceso"),
    RESUELTO("Resuelto");

    companion object {
        fun desdeTexto(valor: String): EstadoTicket {
            return entries.firstOrNull {
                it.name.equals(valor, ignoreCase = true)
            } ?: throw IllegalArgumentException(
                "Estado de incidencia desconocido: $valor"
            )
        }
    }
}

// Tipos reales de incidencia del backend
enum class TipoIncidencia(val etiqueta: String) {
    HARDWARE("Hardware"),
    SOFTWARE("Software"),
    RED("Red"),
    INFRAESTRUCTURA("Infraestructura"),
    OTRO("Otro");

    companion object {
        fun desdeTexto(valor: String): TipoIncidencia {
            return entries.firstOrNull {
                it.name.equals(valor, ignoreCase = true)
            } ?: throw IllegalArgumentException(
                "Tipo de incidencia desconocido: $valor"
            )
        }
    }
}

// Modelo de dominio que utilizará Android
data class Incidencia(
    val idTicket: Int,
    val idUsuarioReporta: Int?,
    val nombreUsuarioReporta: String?,
    val idAmbiente: Int?,
    val nombreAmbiente: String?,
    val idEquipo: Int?,
    val nombreEquipo: String?,
    val tipoIncidencia: TipoIncidencia,
    val descripcion: String,
    val estadoTicket: EstadoTicket,
    val fechaReporte: String?,
    val respuestaEncargado: String?,
    val fechaResolucion: String?,
    val codigoCorrelativo: String? = null
){
    val fechaReporteVisual: String
        get() = formatearFechaHora(fechaReporte)

    val fechaResolucionVisual: String
        get() = formatearFechaHora(fechaResolucion)
}
