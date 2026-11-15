package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val correo: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UsuarioDto
)

@Serializable
data class UsuarioDto(
    val idUsuario: Int,
    val codigoInstitucional: String? = null,
    val nombres: String? = null,
    val apellidos: String? = null,
    val correo: String? = null,
    val rol: String? = null,
    val idAmbiente: Int? = null,
    val nombreAmbiente: String? = null
)