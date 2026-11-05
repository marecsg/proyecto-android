package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.api.apiCall
import com.pe.data.remote.dto.EquipoDto
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.Equipment
import com.pe.domain.model.LabSession
import com.pe.domain.repository.EquipoRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get

class EquipoRepositoryImpl(private val client: HttpClient) : EquipoRepository {
    override suspend fun obtenerEquipos(session: LabSession): List<Equipment> = apiCall {
        client.get("${ApiClient.getBaseUrl()}/api/equipos") {
            bearerAuth(session.token)
        }.body<List<EquipoDto>>().map { it.toDomain() }
    }
}
