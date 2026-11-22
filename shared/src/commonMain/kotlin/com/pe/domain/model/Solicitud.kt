package com.pe.domain.model

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

    private fun formatearFechaHora(fechaIso: String?): String {
        if (fechaIso.isNullOrBlank()) return "Desconocido"

        val secciones = fechaIso.split("T")
        if (secciones.size != 2) return fechaIso

        val bloqueFecha = secciones[0].split("-")
        val fechaFormateada = if (bloqueFecha.size == 3) {
            "${bloqueFecha[2]}/${bloqueFecha[1]}/${bloqueFecha[0]}"
        } else {
            secciones[0]
        }

        val bloqueHora = secciones[1].split(":")
        val hora24 = bloqueHora.getOrNull(0)?.toIntOrNull()
            ?: return fechaFormateada

        val minutos = bloqueHora.getOrNull(1)?.padStart(2, '0') ?: "00"
        val hora12 = if (hora24 % 12 == 0) 12 else hora24 % 12
        val turno = if (hora24 < 12) "AM" else "PM"

        return "$fechaFormateada $hora12:$minutos $turno"
    }
}
