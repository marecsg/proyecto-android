package com.pe.data.repository

import com.pe.data.remote.api.ApiClient
import com.pe.data.remote.dto.AuthResponse
import com.pe.data.remote.dto.LoginRequest
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.Usuario
import com.pe.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import pe.edu.upeu.data.local.SessionManager

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager
) : AuthRepository {

    private val baseUrl = ApiClient.getBaseUrl()
    override suspend fun login(correo: String, password: String): Result<Usuario> {
        return try {

            val response = client.post("$baseUrl/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(correo = correo.trim(), password = password))
            }

            if (response.status.isSuccess()) {

                val authResponse = response.body<AuthResponse>()
                val usuario = authResponse.toDomain()
                sessionManager.saveToken(authResponse.token)
                sessionManager.saveUser(usuario)
                Result.success(usuario)

            } else {

                val errorBody = response.bodyAsText()
                println(">>> ERROR LOGIN HTTP ${response.status}: $errorBody")
                Result.failure(Exception("Error de inicio de sesión (${response.status.value}): $errorBody"))
            }
        } catch (e: Exception) {
            println(">>> EXCEPCIÓN EN LOGIN: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}