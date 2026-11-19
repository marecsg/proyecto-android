
package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.dto.HistorialDto
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.Historial
import com.pe.domain.repository.HistorialRepository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

import kotlinx.coroutines.CancellationException
import pe.edu.upeu.data.local.SessionManager

class HistorialRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager
) : HistorialRepository {

    private val baseUrl = ApiClient.getBaseUrl()

    // Obtener el JWT del usuario autenticado
    private fun obtenerToken(): String {
        return sessionManager.getToken()
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(
                "Debes iniciar sesión para consultar el historial"
            )
    }

    // 1. Consultar historial del estudiante
    override suspend fun obtenerPorEstudiante(
        idEstudiante: Int
    ): Result<List<Historial>> = ejecutar {

        val response = client.get(
            "$baseUrl/api/historial/estudiante/$idEstudiante"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
        }

        response.body<List<HistorialDto>>()
            .map { it.toDomain() }
    }

    // 2. Consultar historial del encargado
    override suspend fun obtenerPorEncargado(
        idEncargado: Int
    ): Result<List<Historial>> = ejecutar {

        val response = client.get(
            "$baseUrl/api/historial/encargado/$idEncargado"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
        }

        response.body<List<HistorialDto>>()
            .map { it.toDomain() }
    }

    // 3. Manejo de errores
    private suspend fun <T> ejecutar(
        bloque: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(bloque())

        } catch (e: CancellationException) {
            throw e

        } catch (e: ResponseException) {
            Result.failure(
                Exception(
                    "Error HTTP ${e.response.status.value}: ${e.message}",
                    e
                )
            )

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
