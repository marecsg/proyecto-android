package com.pe.data.remote.api

import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException

fun mensajeErrorHttp(
    codigo: Int,
    esLogin: Boolean = false
): String {
    return when (codigo) {
        400 -> "Revisa los datos ingresados e inténtalo nuevamente."

        401 -> if (esLogin) {
            "Correo o contraseña incorrectos."
        } else {
            "Tu sesión ha expirado. Inicia sesión nuevamente."
        }

        403 -> "No tienes permiso para realizar esta acción."

        404 -> "No se encontró la información solicitada."

        409 -> "No se puede completar esta operación en el estado actual."

        422 -> "Algunos datos ingresados no son válidos."

        429 -> "Demasiados intentos. Inténtalo más tarde."

        in 500..599 ->
            "El servidor presentó un problema. Inténtalo más tarde."

        else ->
            "No se pudo completar la operación. Inténtalo nuevamente."
    }
}

suspend fun <T> apiCall(
    block: suspend () -> T
): T {
    return try {
        block()

    } catch (e: CancellationException) {
        throw e

    } catch (e: ResponseException) {
        val esLogin = e.response.call.request.url.encodedPath
            .endsWith("/api/auth/login")

        throw IllegalStateException(
            mensajeErrorHttp(e.response.status.value, esLogin),
            e
        )

    } catch (e: Exception) {
        throw IllegalStateException(
            "No se pudo completar la operación. Comprueba tu conexión e inténtalo nuevamente.",
            e
        )
    }
}

