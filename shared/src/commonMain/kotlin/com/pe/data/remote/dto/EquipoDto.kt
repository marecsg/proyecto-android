package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EquipoDto(
    val idEquipo: Int = 0,
    val codigoQr: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val idAmbiente: Int? = null,
    val nombreAmbiente: String? = null,
    val estadoFisico: String? = null,
    val disponibilidad: String? = null
)

@Serializable
data class EquipoUpdateRequest(
    val estadoFisico: String,
    val disponibilidad: String
)