package com.pe.domain.model

import com.pe.utils.FechaUtils.formatearFechaHora

enum class EstadoSolicitud(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada");

    companion object {
        fun desdeTexto(valor: String): EstadoSolicitud {
            return entries.first {
                it.name.equals(valor, ignoreCase = true)
            }
        }
    }
}

data class DetalleSolicitud(
    val idEquipo: Int,
    val codigoQrEquipo: String,
    val nombreEquipo: String
)

data class Solicitud(
    val idSolicitud: Int,
    val idEstudiante: Int,
    val nombreEstudiante: String,
    val idAmbiente: Int,
    val nombreAmbiente: String,
    val motivoSolicitud: String,
    val fechaInicioReserva: String,
    val fechaFinReserva: String,
    val fechaSolicitud: String,
    val estado: EstadoSolicitud,
    val codigoEstudiante: String = "",
    val fechaRespuesta: String? = null,
    val comentarioRespuesta: String? = null,
    val detalles: List<DetalleSolicitud> = emptyList(),
    val codigoCorrelativo: String? = null
) {
    val estaPendiente: Boolean
        get() = estado == EstadoSolicitud.PENDIENTE

    val inicioReservaVisual: String
        get() = formatearFechaHora(fechaInicioReserva)

    val finReservaVisual: String
        get() = formatearFechaHora(fechaFinReserva)

    val fechaSolicitudVisual: String
        get() = formatearFechaHora(fechaSolicitud)

    val fechaRespuestaVisual: String
        get() = formatearFechaHora(fechaRespuesta)


}
