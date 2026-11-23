package com.pe.domain.model
import com.pe.utils.FechaUtils.formatearFechaHora

data class Historial(
    val idHistorial: Int,
    val idUsuarioActor: Int,
    val nombreActor: String,
    val idUsuarioAfectado: Int,
    val nombreAfectado: String,
    val tipoAccion: String,
    val tipoEntidad: String,
    val idEntidad: Int,
    val descripcion: String?,
    val fechaAccion: String
) {
    val actorEsAfectado: Boolean
        get() = idUsuarioActor == idUsuarioAfectado

    val fechaAccionVisual: String
        get() = formatearFechaHora(fechaAccion)

}