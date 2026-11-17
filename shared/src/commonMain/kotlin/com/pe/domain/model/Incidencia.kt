package com.pe.domain.model

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
