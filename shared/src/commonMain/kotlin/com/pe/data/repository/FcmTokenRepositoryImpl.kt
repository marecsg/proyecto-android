package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.dto.FcmTokenRequest
import com.pe.domain.repository.FcmTokenRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import pe.edu.upeu.data.local.SessionManager

class FcmTokenRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager
) : FcmTokenRepository {

    override suspend fun registrar(token: String): Result<Unit> {
        return try {
            val jwt = sessionManager.getToken()
                ?: return Result.failure(
                    IllegalStateException("No hay sesión activa")
                )

            val response = client.post(
                "${ApiClient.getBaseUrl()}/api/usuarios/fcm-token"
            ) {
                header(HttpHeaders.Authorization, "Bearer $jwt")
                contentType(ContentType.Application.Json)
                setBody(FcmTokenRequest(token))
            }

            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(
                    IllegalStateException(
                        "Error registrando FCM: ${response.status.value}"
                    )
                )
            }

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun desvincular(token: String): Result<Unit> {
        return try {
            val jwt = sessionManager.getToken()
                ?: return Result.failure(
                    IllegalStateException("No hay sesión activa")
                )

            val response = client.post(
                "${ApiClient.getBaseUrl()}/api/usuarios/fcm-token/desvincular"
            ) {
                header(HttpHeaders.Authorization, "Bearer $jwt")
                contentType(ContentType.Application.Json)
                setBody(FcmTokenRequest(token))
            }

            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(
                    IllegalStateException(
                        "Error desvinculando FCM: ${response.status.value}"
                    )
                )
            }

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
