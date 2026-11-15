package com.pe.domain.model

enum class DisponibilidadEquipo(val etiqueta: String) {
    DISPONIBLE("Disponible"),
    PRESTADO("Prestado"),
    NO_DISPONIBLE("No disponible");

    companion object {
        fun desdeTexto(valor: String?): DisponibilidadEquipo {
            return entries.find { it.name.equals(valor, ignoreCase = true) } ?: DISPONIBLE
        }
    }
}

enum class EstadoFisicoEquipo(val etiqueta: String) {
    OPERATIVO("Operativo"),
    MANTENIMIENTO("Mantenimiento"),
    DEFECTUOSO("Defectuoso");

    companion object {
        fun desdeTexto(valor: String?): EstadoFisicoEquipo {
            return entries.find { it.name.equals(valor, ignoreCase = true) } ?: OPERATIVO
        }
    }
}

data class Equipo(
    val idEquipo: Int,
    val codigoQr: String,
    val nombre: String,
    val descripcion: String,
    val idAmbiente: Int,
    val nombreAmbiente: String,
    val estadoFisico: EstadoFisicoEquipo,
    val disponibilidad: DisponibilidadEquipo
) {
    val estaDisponible: Boolean
        get() = disponibilidad == DisponibilidadEquipo.DISPONIBLE &&
                estadoFisico == EstadoFisicoEquipo.OPERATIVO
}