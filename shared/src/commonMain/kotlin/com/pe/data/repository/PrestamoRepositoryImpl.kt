package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.api.apiCall
import com.pe.data.remote.dto.*
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.Equipment
import com.pe.domain.model.LabSession
import com.pe.domain.model.Loan
import com.pe.domain.model.LoanDraft
import com.pe.domain.repository.PrestamoRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PrestamoRepositoryImpl(private val client: HttpClient) : PrestamoRepository {
    private fun fechaIso(date: String): String {
        val d = date.trim().split('/')
        return "${d[2]}-${d[1]}-${d[0]}"
    }
    override suspend fun obtenerDelEstudiante(session: LabSession): List<Loan> = apiCall {
        client.get("${ApiClient.getBaseUrl()}/api/solicitudes/estudiante/${session.id}") {
            bearerAuth(session.token)
        }.body<List<PrestamoDto>>().map { it.toDomain() }.sortedByDescending { it.remoteId }
    }

    override suspend fun obtenerDelEncargado(session: LabSession): List<Loan> = apiCall {
        client.get("${ApiClient.getBaseUrl()}/api/solicitudes/encargado/${session.id}") {
            bearerAuth(session.token)
        }.body<List<PrestamoDto>>().map { it.toDomain() }.sortedByDescending { it.remoteId }
    }

    override suspend fun solicitar(session: LabSession, equipo: Equipment, draft: LoanDraft): Loan = apiCall {
        val data = CrearPrestamoDto(
            estudiante = IdUsuarioDto(session.id),
            equipo = IdEquipoDto(equipo.remoteId),
            motivoSolicitud = "Curso: ${draft.course}; Docente: ${draft.teacher}; Motivo: ${draft.reason}",
            fechaInicioUso = fechaIso(draft.date) + "T" + draft.startTime + ":00",
            fechaFinUso = fechaIso(draft.returnDate) + "T" + draft.endTime + ":00"
        )
        val created = client.post("${ApiClient.getBaseUrl()}/api/solicitudes") {
            bearerAuth(session.token)
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body<PrestamoDto>()
        // Al crear, Spring Boot puede devolver relaciones parciales; conservamos datos locales completos.
        created.toDomain().copy(
            equipment = equipo,
            requester = session.nombre,
            course = draft.course,
            teacher = draft.teacher,
            reason = data.motivoSolicitud,
            date = draft.date,
            returnDate = draft.returnDate
        )
    }

    override suspend fun responder(session: LabSession, idSolicitud: Int, aceptar: Boolean): Loan = apiCall {
        val estado = if (aceptar) "ACEPTADA" else "RECHAZADA"
        val data = ResponderPrestamoDto(
            estadoSolicitud = estado,
            encargadoAprobacion = IdUsuarioDto(session.id),
            comentarioAprobacion = if (aceptar) "Solicitud aprobada por encargado" else "Solicitud rechazada por encargado"
        )
        client.put("${ApiClient.getBaseUrl()}/api/solicitudes/$idSolicitud/responder") {
            bearerAuth(session.token)
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body<PrestamoDto>().toDomain()
    }
}
