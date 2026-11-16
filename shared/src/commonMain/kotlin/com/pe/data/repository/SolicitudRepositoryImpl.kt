package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.dto.*
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.EstadoSolicitud
import com.pe.domain.model.Solicitud
import com.pe.domain.repository.SolicitudRepository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.patch
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

import kotlinx.coroutines.CancellationException
import pe.edu.upeu.data.local.SessionManager

class SolicitudRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager
) : SolicitudRepository {

    private val baseUrl = ApiClient.getBaseUrl()

    private fun obtenerToken(): String {
        return sessionManager.getToken()
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(
                "Debes iniciar sesión para continuar"
            )
    }

    // 1. Obtener una solicitud por ID
    override suspend fun obtenerPorId(
        idSolicitud: Int
    ): Result<Solicitud> = ejecutar {

        val response = client.get(
            "$baseUrl/api/solicitudes/$idSolicitud"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
        }

        response.body<SolicitudDto>().toDomain()
    }

    // 2. Listar solicitudes de un estudiante
    override suspend fun obtenerPorEstudiante(
        idEstudiante: Int
    ): Result<List<Solicitud>> = ejecutar {

        val response = client.get(
            "$baseUrl/api/solicitudes/estudiante/$idEstudiante"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
        }

        response.body<List<SolicitudDto>>()
            .map { it.toDomain() }
    }

    // 3. Listar solicitudes de un ambiente
    override suspend fun obtenerPorAmbiente(
        idAmbiente: Int
    ): Result<List<Solicitud>> = ejecutar {

        val response = client.get(
            "$baseUrl/api/solicitudes/ambiente/$idAmbiente"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
        }

        response.body<List<SolicitudDto>>()
            .map { it.toDomain() }
    }

    // 4. Crear una solicitud
    override suspend fun crearSolicitud(
        idEstudiante: Int,
        idAmbiente: Int,
        motivoSolicitud: String,
        fechaInicioReserva: String,
        fechaFinReserva: String,
        idsEquipos: List<Int>
    ): Result<Solicitud> = ejecutar {

        require(idsEquipos.isNotEmpty()) {
            "Debes seleccionar al menos un equipo"
        }

        val request = CrearSolicitudRequest(
            idEstudiante = idEstudiante,
            idAmbiente = idAmbiente,
            motivoSolicitud = motivoSolicitud,
            fechaInicioReserva = fechaInicioReserva,
            fechaFinReserva = fechaFinReserva,
            equipos = idsEquipos.map { id ->
                SolicitudEquipoRequest(idEquipo = id)
            }
        )

        val response = client.post(
            "$baseUrl/api/solicitudes"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        response.body<SolicitudDto>().toDomain()
    }

    // 5. Cambiar estado de una solicitud
    override suspend fun cambiarEstado(
        idSolicitud: Int,
        nuevoEstado: EstadoSolicitud,
        comentarioRespuesta: String?
    ): Result<Solicitud> = ejecutar {

        val request = ActualizarEstadoSolicitudRequest(
            estadoSolicitud = nuevoEstado.name,
            comentarioRespuesta = comentarioRespuesta
        )

        val response = client.patch(
            "$baseUrl/api/solicitudes/$idSolicitud/estado"
        ) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${obtenerToken()}"
            )
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        response.body<SolicitudDto>().toDomain()
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
