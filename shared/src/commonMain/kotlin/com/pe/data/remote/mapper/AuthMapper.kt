package com.pe.data.remote.mapper

import com.pe.data.remote.dto.AuthResponse
import com.pe.data.remote.dto.UsuarioDto
import com.pe.domain.model.Usuario


fun AuthResponse.toDomain(): Usuario {
    return this.user.toDomain()
}
fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        idUsuario = idUsuario,
        codigoInstitucional = codigoInstitucional.orEmpty(),
        nombres = nombres.orEmpty(),
        apellidos = apellidos.orEmpty(),
        correo = correo.orEmpty(),
        rol = rol?.uppercase() ?: "ESTUDIANTE",
        idAmbiente = idAmbiente,
        nombreAmbiente = nombreAmbiente
    )
}