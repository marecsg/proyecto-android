package com.pe.domain.repository

import com.pe.domain.model.LabSession

interface AuthRepository {
    suspend fun login(correo: String, password: String): LabSession
}
