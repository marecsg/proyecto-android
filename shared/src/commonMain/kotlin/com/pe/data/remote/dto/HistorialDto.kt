package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HistorialDto(
    val idHistorial: Int,
    val idUsuarioActor: Int,
    val nombreActor: String,
    val idUsuarioAfectado: Int,
    val nombreAfectado: String,
    val tipoAccion: String,
    val tipoEntidad: String,
    val idEntidad: Int,
    val descripcion: String? = null,
    val fechaAccion: String
)