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
data class RolDto(
    val idRol: Int,
    val nombre: String
)

@Serializable
data class UsuarioDto(
    val idUsuario: Int,
    val codigoInstitucional: String? = null,
    val nombres: String? = null,
    val apellidos: String? = null,
    val correo: String? = null,
    val rol: RolDto? = null,          // Para cuando viene de las solicitudes
    val rolString: String? = null     // Para cuando viene directamente del login
) {
    // Propiedad calculada que busca primero en rolString (login) y si no, en el objeto rol
    val finalRolString: String
        get() = rolString?.uppercase() ?: rol?.nombre?.uppercase() ?: "ESTUDIANTE"
}