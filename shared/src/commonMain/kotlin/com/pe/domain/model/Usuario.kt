package com.pe.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val idUsuario: Int,
    val codigoInstitucional: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val rol: String,
    val token: String? = null
)