
package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.dto.ActualizarEstadoPrestamoRequest
import com.pe.data.remote.dto.CrearPrestamoRequest
import com.pe.data.remote.dto.DetalleDevolucionRequest
import com.pe.data.remote.dto.PrestamoDto
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.Prestamo
import com.pe.domain.repository.PrestamoRepository
import com.pe.domain.repository.PrestamoRepository.DevolucionItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import pe.edu.upeu.data.local.SessionManager

class PrestamoRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager
) : PrestamoRepository {

    private val baseUrl = ApiClient.getBaseUrl()

    private fun obtenerToken(): String {
        return sessionManager.getToken()
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException(
                "Debes iniciar sesión para continuar"
            )
    }

    override suspend fun obtenerPrestamoPorId(
        idPrestamo: Int
    ): Result<Prestamo> {
        return try {
            val response = client.get("$baseUrl/api/prestamos/$idPrestamo") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${obtenerToken()}")
            }

            if (response.status.isSuccess()) {
                val dto = response.body<PrestamoDto>()
                Result.success(dto.toDomain())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Error al obtener préstamo (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerPorEstudiante(
        idEstudiante: Int
    ): Result<List<Prestamo>> {
        return try {
            val response = client.get("$baseUrl/api/prestamos/estudiante/$idEstudiante") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${obtenerToken()}")
            }

            if (response.status.isSuccess()) {
                val dtos = response.body<List<PrestamoDto>>()
                Result.success(
                    dtos.map { it.toDomain() }.sortedByDescending { it.idPrestamo }
                )
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Error al obtener préstamos del estudiante (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerPorAmbiente(
        idAmbiente: Int
    ): Result<List<Prestamo>> {
        return try {
            val response = client.get("$baseUrl/api/prestamos/ambiente/$idAmbiente") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${obtenerToken()}")
            }

            if (response.status.isSuccess()) {
                val dtos = response.body<List<PrestamoDto>>()
                Result.success(
                    dtos.map { it.toDomain() }.sortedByDescending { it.idPrestamo }
                )
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Error al obtener préstamos del ambiente (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Aprobar solicitud y generar préstamo
    override suspend fun crearDesdeSolicitud(
        idSolicitud: Int,
        comentarioRespuesta: String?
    ): Result<Prestamo> {
        return try {
            val request = CrearPrestamoRequest(
                idSolicitud = idSolicitud,
                comentarioRespuesta = comentarioRespuesta
            )

            val response = client.post("$baseUrl/api/prestamos/aprobar") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${obtenerToken()}")
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val dto = response.body<PrestamoDto>()
                Result.success(dto.toDomain())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Error al aprobar solicitud (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Iniciar o cancelar préstamo
    override suspend fun cambiarEstado(
        idPrestamo: Int,
        nuevoEstado: EstadoPrestamo
    ): Result<Prestamo> {
        return try {
            val request = ActualizarEstadoPrestamoRequest(
                estadoPrestamo = nuevoEstado.name
            )

            val response = client.patch("$baseUrl/api/prestamos/$idPrestamo/estado") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${obtenerToken()}")
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val dto = response.body<PrestamoDto>()
                Result.success(dto.toDomain())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Error al cambiar estado (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Registrar devolución
    override suspend fun registrarDevolucion(
        idPrestamo: Int,
        estadoPrestamo: EstadoPrestamo,
        detallesDevolucion: List<DevolucionItem>
    ): Result<Prestamo> {
        return try {
            val request = ActualizarEstadoPrestamoRequest(
                estadoPrestamo = estadoPrestamo.name,
                detallesDevolucion = detallesDevolucion.map { item ->
                    DetalleDevolucionRequest(
                        idDetalle = item.idDetalle,
                        estadoItem = item.estadoItem.name,
                        estadoFisicoAlDevolver = item.estadoFisicoAlDevolver?.name,
                        comentarioDevolucion = item.comentarioDevolucion
                    )
                }
            )

            val response = client.patch("$baseUrl/api/prestamos/$idPrestamo/devolucion") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${obtenerToken()}")
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val dto = response.body<PrestamoDto>()
                Result.success(dto.toDomain())
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(Exception("Error al registrar devolución (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
