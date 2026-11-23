package com.pe.domain.model

import com.pe.utils.FechaUtils.formatearFechaHora

enum class EstadoPrestamo(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_CURSO("En curso"),
    FINALIZADO("Finalizado"),
    VENCIDO("Vencido"),
    CANCELADO("Cancelado");

    companion object {
        fun desdeTexto(valor: String?): EstadoPrestamo {
            return entries.find { it.name.equals(valor, ignoreCase = true) } ?: PENDIENTE
        }
    }
}

enum class EstadoItem(val etiqueta: String) {
    DEVUELTO("Devuelto"),
    NO_DEVUELTO("No devuelto");

    companion object {
        fun desdeTexto(valor: String?): EstadoItem? {
            if (valor.isNullOrBlank()) return null
            return entries.find { it.name.equals(valor, ignoreCase = true) }
        }
    }
}

data class DetallePrestamo(
    val idDetalle: Int,
    val idEquipo: Int,
    val codigoQrEquipo: String,
    val nombreEquipo: String,
    val estadoItem: EstadoItem? = null,
    val estadoFisicoAlDevolver: String? = null,
    val comentarioDevolucion: String? = null
) {
    val etiquetaEstado: String
        get() = estadoItem?.etiqueta ?: "En curso"

    val yaFueEvaluado: Boolean
        get() = estadoItem != null
}

data class Prestamo(
    val idPrestamo: Int,
    val idSolicitud: Int,
    val nombreEstudiante: String,
    val codigoEstudiante: String,
    val nombreAmbiente: String,
    val idEncargadoAprobacion: Int? = null,
    val nombreEncargadoAprobacion: String,
    val nombreEncargadoInicio: String? = null,
    val idEncargadoDevolucion: Int? = null,
    val nombreEncargadoDevolucion: String? = null,
    val fechaInicioUso: String? = null,
    val fechaFinUso: String? = null,
    val estado: EstadoPrestamo,
    val detalles: List<DetallePrestamo> = emptyList(),
    val codigoCorrelativo: String? = null
) {
    val estaActivo: Boolean
        get() = estado == EstadoPrestamo.EN_CURSO || estado == EstadoPrestamo.PENDIENTE

    val resumenEquipos: String
        get() = when (detalles.size) {
            0 -> "Sin equipos"
            1 -> detalles.first().nombreEquipo
            else -> "${detalles.first().nombreEquipo} (+${detalles.size - 1} más)"
        }

    val fechaInicoVisual: String
        get() = formatearFechaHora(fechaInicioUso)

    val fechaFinVisual: String
        get() = formatearFechaHora(fechaFinUso)
}