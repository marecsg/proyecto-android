package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val correo: String, val password: String)

@Serializable
data class LoginResponseDto(
    val token: String = "",
    val nombres: String = "",
    val correo: String = "",
    val rol: String = ""
)

@Serializable
data class UsuarioDto(
    val idUsuario: Int = 0,
    val nombres: String? = null,
    val apellidos: String? = null
)
