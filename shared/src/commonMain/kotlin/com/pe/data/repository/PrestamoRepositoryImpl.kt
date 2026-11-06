package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.dto.*
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.Equipment
import com.pe.domain.model.Loan
import com.pe.domain.model.LoanDraft
import com.pe.domain.model.Usuario
import com.pe.domain.repository.PrestamoRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
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

    private fun fechaIso(date: String): String {
        val d = date.trim().split('/')
        return "${d[2]}-${d[1]}-${d[0]}"
    }

    private fun obtenerToken(): String {
        return sessionManager.getToken() ?: ""
    }

    override suspend fun obtenerDelEstudiante(usuario: Usuario): Result<List<Loan>> {
        return try {
            val userId = usuario.idUsuario ?: 0L
            val token = obtenerToken()
            println("DEBUG PRESTAMO [Estudiante] -> TOKEN: '$token'")

            val response = client.get("$baseUrl/api/solicitudes/estudiante/$userId") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token") // 👈 Agregado "Bearer "
            }
            println("DEBUG PRESTAMO [Estudiante] -> STATUS: ${response.status}")

            if (response.status.isSuccess()) {
                val dtos = response.body<List<PrestamoDto>>()
                println("DEBUG PRESTAMO [Estudiante] -> Cantidad obtenida: ${dtos.size}")
                Result.success(dtos.map { it.toDomain() }.sortedByDescending { it.remoteId })
            } else {
                val errorBody = try { response.body<String>() } catch(_: Exception) { "No body" }
                println("DEBUG PRESTAMO [Estudiante] -> ERROR BODY: $errorBody")
                Result.failure(Exception("Error al obtener préstamos de estudiante: ${response.status}"))
            }
        } catch (e: Exception) {
            println("DEBUG PRESTAMO [Estudiante] -> EXCEPTION: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun obtenerDelEncargado(usuario: Usuario): Result<List<Loan>> {
        return try {
            val userId = usuario.idUsuario ?: 0L
            val token = obtenerToken()
            println("DEBUG PRESTAMO [Encargado] -> TOKEN: '$token'")

            val response = client.get("$baseUrl/api/solicitudes/encargado/$userId") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            println("DEBUG PRESTAMO [Encargado] -> STATUS: ${response.status}")

            if (response.status.isSuccess()) {
                val dtos = response.body<List<PrestamoDto>>()
                println("DEBUG PRESTAMO [Encargado] -> Cantidad obtenida: ${dtos.size}")
                Result.success(dtos.map { it.toDomain() }.sortedByDescending { it.remoteId })
            } else {
                val errorBody = try { response.body<String>() } catch(_: Exception) { "No body" }
                println("DEBUG PRESTAMO [Encargado] -> ERROR BODY: $errorBody")
                Result.failure(Exception("Error al obtener solicitudes de encargado: ${response.status}"))
            }
        } catch (e: Exception) {
            println("DEBUG PRESTAMO [Encargado] -> EXCEPTION: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun solicitar(usuario: Usuario, equipo: Equipment, draft: LoanDraft): Result<Loan> {
        return try {
            val userId = usuario.idUsuario
            val token = obtenerToken()
            println("DEBUG PRESTAMO [Solicitar] -> TOKEN: '$token'")

            val data = CrearPrestamoDto(
                estudiante = IdUsuarioDto(userId),
                equipo = IdEquipoDto(equipo.remoteId),
                motivoSolicitud = "Curso: ${draft.course}; Docente: ${draft.teacher}; Motivo: ${draft.reason}",
                fechaInicioUso = fechaIso(draft.date) + "T" + draft.startTime + ":00",
                fechaFinUso = fechaIso(draft.returnDate) + "T" + draft.endTime + ":00"
            )

            val response = client.post("$baseUrl/api/solicitudes") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(data)
            }
            println("DEBUG PRESTAMO [Solicitar] -> STATUS: ${response.status}")

            if (response.status.isSuccess()) {
                val created = response.body<PrestamoDto>()
                val domainLoan = created.toDomain().copy(
                    equipment = equipo,
                    requester = usuario.nombres,
                    course = draft.course,
                    teacher = draft.teacher,
                    reason = data.motivoSolicitud,
                    date = draft.date,
                    returnDate = draft.returnDate
                )
                Result.success(domainLoan)
            } else {
                val errorBody = try { response.body<String>() } catch(_: Exception) { "No body" }
                println("DEBUG PRESTAMO [Solicitar] -> ERROR BODY: $errorBody")
                Result.failure(Exception("Error al solicitar préstamo: ${response.status}"))
            }
        } catch (e: Exception) {
            println("DEBUG PRESTAMO [Solicitar] -> EXCEPTION: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun responder(usuario: Usuario, idSolicitud: Int, aceptar: Boolean): Result<Loan> {
        return try {
            val userId = usuario.idUsuario
            val token = obtenerToken()
            println("DEBUG PRESTAMO [Responder] -> TOKEN: '$token'")

            val estado = if (aceptar) "ACEPTADA" else "RECHAZADA"
            val data = ResponderPrestamoDto(
                estadoSolicitud = estado,
                encargadoAprobacion = IdUsuarioDto(userId),
                comentarioAprobacion = if (aceptar) "Solicitud aprobada por encargado" else "Solicitud rechazada por encargado"
            )

            val response = client.put("$baseUrl/api/solicitudes/$idSolicitud/responder") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(data)
            }
            println("DEBUG PRESTAMO [Responder] -> STATUS: ${response.status}")

            if (response.status.isSuccess()) {
                val dto = response.body<PrestamoDto>()
                Result.success(dto.toDomain())
            } else {
                val errorBody = try { response.body<String>() } catch(_: Exception) { "No body" }
                println("DEBUG PRESTAMO [Responder] -> ERROR BODY: $errorBody")
                Result.failure(Exception("Error al responder solicitud: ${response.status}"))
            }
        } catch (e: Exception) {
            println("DEBUG PRESTAMO [Responder] -> EXCEPTION: ${e.message}")
            Result.failure(e)
        }
    }
}