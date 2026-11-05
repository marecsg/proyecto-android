package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AmbienteDto(val nombre: String? = null)

@Serializable
data class EquipoDto(
    val idEquipo: Int = 0,
    val codigoQr: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val ambiente: AmbienteDto? = null,
    val estadoFisico: String? = null,
    val disponibilidad: String? = null
)
