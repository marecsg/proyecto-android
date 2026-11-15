package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.dto.EquipoDto
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.Equipo
import com.pe.domain.repository.EquipoRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import pe.edu.upeu.data.local.SessionManager

class EquipoRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager
) : EquipoRepository {

    private val baseUrl = ApiClient.getBaseUrl()

    override suspend fun obtenerEquipos(): Result<List<Equipo>> {
        return try {
            val token = sessionManager.getToken().orEmpty()
            val response = client.get("$baseUrl/api/equipos") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            if (response.status.isSuccess()) {
                val equipoDtos = response.body<List<EquipoDto>>()
                Result.success(equipoDtos.map { it.toDomain() })
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Error al obtener equipos (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerEquipoPorId(idEquipo: Int): Result<Equipo> {
        return try {
            val token = sessionManager.getToken().orEmpty()
            val response = client.get("$baseUrl/api/equipos/$idEquipo") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            if (response.status.isSuccess()) {
                val dto = response.body<EquipoDto>()
                Result.success(dto.toDomain())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Equipo no encontrado (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerEquipoPorQr(codigoQr: String): Result<Equipo> {
        return try {
            val token = sessionManager.getToken().orEmpty()
            val response = client.get("$baseUrl/api/equipos/qr/$codigoQr") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            if (response.status.isSuccess()) {
                val dto = response.body<EquipoDto>()
                Result.success(dto.toDomain())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("QR no registrado (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}