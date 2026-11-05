package com.pe.data.remote.api

import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException

suspend fun <T> apiCall(block: suspend () -> T): T = try {
    block()
} catch (ex: CancellationException) {
    throw ex
} catch (ex: ResponseException) {
    val code = ex.response.status.value
    throw IllegalStateException(when (code) {
        401 -> "Credenciales inválidas o sesión expirada (401)"
        403 -> "Acceso no permitido (403)"
        404 -> "No se encontró el recurso solicitado (404)"
        else -> "Error del servidor: HTTP $code"
    }, ex)
} catch (ex: Exception) {
    throw IllegalStateException(ex.message ?: "No hay conexión con Spring Boot", ex)
}
