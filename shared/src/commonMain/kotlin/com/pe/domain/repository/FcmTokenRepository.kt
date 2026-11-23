package com.pe.domain.repository

interface FcmTokenRepository {

    suspend fun registrar(token: String): Result<Unit>
    suspend fun desvincular(token: String): Result<Unit>

}
