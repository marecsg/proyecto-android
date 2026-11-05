package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PrestamoDto(
    val idSolicitud: Int = 0,
    val estudiante: UsuarioDto? = null,
    val equipo: EquipoDto? = null,
    val motivoSolicitud: String = "",
    val fechaInicioUso: String = "",
    val fechaFinUso: String = "",
    val estadoSolicitud: String = "PENDIENTE"
)

@Serializable
data class IdUsuarioDto(val idUsuario: Int)

@Serializable
data class IdEquipoDto(val idEquipo: Int)

@Serializable
data class CrearPrestamoDto(
    val estudiante: IdUsuarioDto,
    val equipo: IdEquipoDto,
    val motivoSolicitud: String,
    val fechaInicioUso: String,
    val fechaFinUso: String
)

@Serializable
data class ResponderPrestamoDto(
    val estadoSolicitud: String,
    val encargadoAprobacion: IdUsuarioDto,
    val comentarioAprobacion: String
)
