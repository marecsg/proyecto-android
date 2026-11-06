package com.pe.domain.repository

import com.pe.domain.model.Usuario

interface AuthRepository {
    suspend fun login(correo: String, password: String): Result<Usuario>
}
