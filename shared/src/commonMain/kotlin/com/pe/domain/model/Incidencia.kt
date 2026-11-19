package com.pe.domain.model

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
    val fechaResolucion: String?
)

// ==========================================
// MODELOS ANTIGUOS
// Se conservan por compatibilidad.
// No utilizarlos en la nueva implementación.
// ==========================================

enum class IncidentStatus(val label: String) {
    REPORTED("Reportada"),
    IN_REVIEW("En revisión"),
    RESOLVED("Resuelta")
}

data class IncidentDraft(
    val equipment: Equipo,
    val type: String,
    val description: String,
    val date: String
)

data class Incident(
    val id: String,
    val equipment: Equipo,
    val reporter: String,
    val type: String,
    val description: String,
    val date: String,
    val status: IncidentStatus
)

// Modelos de historial existentes
enum class HistoryEntryType(val label: String) {
    LOAN("Préstamo"),
    INCIDENT("Incidencia")
}

data class HistoryEntry(
    val id: String,
    val type: HistoryEntryType,
    val title: String,
    val description: String,
    val date: String,
    val status: String
)