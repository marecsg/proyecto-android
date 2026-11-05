package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.api.apiCall
import com.pe.data.remote.dto.LoginRequestDto
import com.pe.data.remote.dto.LoginResponseDto
import com.pe.data.remote.dto.UsuarioDto
import com.pe.domain.model.LabSession
import com.pe.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRepositoryImpl(private val client: HttpClient) : AuthRepository {
    override suspend fun login(correo: String, password: String): LabSession = apiCall {
        require(correo.isNotBlank() && password.isNotBlank()) { "Ingresa correo y contraseña" }
        val correoLimpio = correo.trim()
        val auth = client.post("${ApiClient.getBaseUrl()}/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(correoLimpio, password))
        }.body<LoginResponseDto>()
        require(auth.token.isNotBlank()) { "El servidor no devolvió un token" }

        // La respuesta de /login no incluye idUsuario; se obtiene en una segunda consulta.
        val usuario = client.get("${ApiClient.getBaseUrl()}/api/usuarios/correo/$correoLimpio") {
            bearerAuth(auth.token)
        }.body<UsuarioDto>()
        require(usuario.idUsuario > 0) { "No se encontró el ID del usuario" }
        val fullName = listOf(usuario.nombres.orEmpty(), usuario.apellidos.orEmpty())
            .filter { it.isNotBlank() }.joinToString(" ")
        LabSession(
            id = usuario.idUsuario,
            nombre = fullName.ifBlank { auth.nombres },
            correo = auth.correo.ifBlank { correoLimpio },
            rol = auth.rol,
            token = auth.token
        )
    }
}
