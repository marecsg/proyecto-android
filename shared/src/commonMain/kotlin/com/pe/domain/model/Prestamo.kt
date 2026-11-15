package com.pe.domain.model

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
    val idEncargadoDevolucion: Int? = null,
    val nombreEncargadoDevolucion: String? = null,
    val fechaInicioUso: String,
    val fechaFinUso: String,
    val comentarioAprobacion: String? = null,
    val estado: EstadoPrestamo,
    val detalles: List<DetallePrestamo> = emptyList()
) {
    val estaActivo: Boolean
        get() = estado == EstadoPrestamo.EN_CURSO || estado == EstadoPrestamo.PENDIENTE

    val resumenEquipos: String
        get() = when (detalles.size) {
            0 -> "Sin equipos"
            1 -> detalles.first().nombreEquipo
            else -> "${detalles.first().nombreEquipo} (+${detalles.size - 1} más)"
        }

    val fechaVisual: String
        get() {
            val partes = fechaInicioUso.substringBefore('T').split('-')
            return if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fechaInicioUso
        }

    val horaInicio: String
        get() = fechaInicioUso.substringAfter('T', "--:--").take(5)

    val horaFin: String
        get() = fechaFinUso.substringAfter('T', "--:--").take(5)

}