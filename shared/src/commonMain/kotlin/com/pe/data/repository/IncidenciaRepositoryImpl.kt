
package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.dto.TicketIncidenciaDto
import com.pe.data.remote.dto.CrearIncidenciaRequest
import com.pe.data.remote.dto.ResolucionIncidenciaRequest
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.Incidencia
import com.pe.domain.model.EstadoTicket
import com.pe.domain.model.TipoIncidencia
import com.pe.domain.repository.IncidenciaRepository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

import kotlinx.coroutines.CancellationException
import pe.edu.upeu.data.local.SessionManager

class IncidenciaRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager
) : IncidenciaRepository {

    private val baseUrl = ApiClient.getBaseUrl()

    // Obtener el JWT del usuario autenticado
    private fun obtenerToken(): String {
        return sessionManager.getToken()
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(
                "Debes iniciar sesión para continuar"
            )
    }

    // 1. Consultar incidencia por ID
    override suspend fun obtenerPorId(
        idTicket: Int
    ): Result<Incidencia> = ejecutar {

        val response = client.get(
            "$baseUrl/api/incidencias/$idTicket"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
        }

        response.body<TicketIncidenciaDto>().toDomain()
    }

    // 2. Consultar incidencias por ambiente
    override suspend fun obtenerPorAmbiente(
        idAmbiente: Int
    ): Result<List<Incidencia>> = ejecutar {

        val response = client.get(
            "$baseUrl/api/incidencias/ambiente/$idAmbiente"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
        }

        response.body<List<TicketIncidenciaDto>>()
            .map { it.toDomain() }
    }

    // 3. Registrar una incidencia
    override suspend fun registrarIncidencia(
        idUsuarioReporta: Int,
        idAmbiente: Int,
        idEquipo: Int?,
        tipoIncidencia: TipoIncidencia,
        descripcion: String
    ): Result<Incidencia> = ejecutar {

        require(descripcion.isNotBlank()) {
            "La descripción no puede estar vacía"
        }

        val request = CrearIncidenciaRequest(
            idUsuarioReporta = idUsuarioReporta,
            idAmbiente = idAmbiente,
            idEquipo = idEquipo,
            tipoIncidencia = tipoIncidencia.name,
            descripcion = descripcion.trim()
        )

        val response = client.post(
            "$baseUrl/api/incidencias"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        response.body<TicketIncidenciaDto>().toDomain()
    }

    // 4. Actualizar estado y resolución
    override suspend fun actualizarResolucion(
        idTicket: Int,
        nuevoEstado: EstadoTicket,
        respuestaEncargado: String?
    ): Result<Incidencia> = ejecutar {

        require(
            nuevoEstado == EstadoTicket.EN_PROCESO ||
                    nuevoEstado == EstadoTicket.RESUELTO
        ) {
            "Solo puedes poner en proceso o resolver"
        }

        if (nuevoEstado == EstadoTicket.RESUELTO) {
            require(!respuestaEncargado.isNullOrBlank()) {
                "Debes escribir una respuesta para resolver"
            }
        }

        val request = ResolucionIncidenciaRequest(
            respuestaEncargado = respuestaEncargado
                ?.trim()
                ?.takeIf { it.isNotEmpty() },
            estadoTicket = nuevoEstado.name
        )

        val response = client.put(
            "$baseUrl/api/incidencias/$idTicket/resolucion"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        response.body<TicketIncidenciaDto>().toDomain()
    }

    // Manejo común de errores
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
