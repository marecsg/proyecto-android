package com.pe.data.remote.mapper

import com.pe.data.remote.dto.AuthResponse
import com.pe.data.remote.dto.UsuarioDto
import com.pe.domain.model.Usuario


fun AuthResponse.toDomain(): Usuario {
    return Usuario(
        idUsuario = this.user.idUsuario,
        codigoInstitucional = this.user.codigoInstitucional ?: "",
        nombres = this.user.nombres ?: "",
        apellidos = this.user.apellidos ?: "",
        correo = this.user.correo ?: "",
        rol = this.user.finalRolString, // 👈 Usamos la propiedad unificada
        token = this.token
    )
}