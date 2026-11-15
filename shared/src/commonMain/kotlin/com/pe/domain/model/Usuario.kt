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
    val idAmbiente: Int? = null,
    val nombreAmbiente: String? = null
) {
    val nombreCompleto: String
        get() = "$nombres $apellidos".trim()

    val esEncargado: Boolean
        get() = rol.equals("ENCARGADO", ignoreCase = true)
}