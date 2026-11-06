package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.dto.EquipoDto
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.Equipment
import com.pe.domain.repository.EquipoRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
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

    override suspend fun obtenerEquipos(): Result<List<Equipment>> {
        return try {
            val token = sessionManager.getToken() ?: ""
            println("DEBUG EQUIPOS -> TOKEN: '$token'")

            val response = client.get("$baseUrl/api/equipos") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            println("DEBUG EQUIPOS -> STATUS: ${response.status}")

            if (response.status.isSuccess()) {
                val equipoDtos = response.body<List<EquipoDto>>()
                println("DEBUG EQUIPOS -> Cantidad obtenida: ${equipoDtos.size}")
                Result.success(equipoDtos.map { it.toDomain() })
            } else {
                val errorBody = try { response.body<String>() } catch(_: Exception) { "No body" }
                println("DEBUG EQUIPOS -> ERROR BODY: $errorBody")
                Result.failure(Exception("Failed to fetch equipos: ${response.status}"))
            }
        } catch (e: Exception) {
            println("DEBUG EQUIPOS -> EXCEPTION: ${e.message}")
            Result.failure(e)
        }
    }
}