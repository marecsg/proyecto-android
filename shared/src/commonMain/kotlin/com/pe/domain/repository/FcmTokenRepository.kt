package com.pe.domain.repository

interface FcmTokenRepository {

    suspend fun registrar(token: String): Result<Unit>

}
