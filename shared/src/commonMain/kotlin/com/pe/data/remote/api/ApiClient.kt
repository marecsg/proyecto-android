package com.pe.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object ApiClient {

    //prueba emulador
    //const val BASE_URL = "http://10.0.2.2:8080"

    //prueba con celular (se usa la ip de la pc)
    const val BASE_URL = "http://192.168.1.38:8080"

    val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            expectSuccess = true

            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, request ->

                    val error = exception as? ResponseException
                        ?: return@handleResponseExceptionWithRequest

                    val esLogin = request.url.encodedPath
                        .endsWith("/api/auth/login")

                    throw IllegalStateException(
                        mensajeErrorHttp(
                            codigo = error.response.status.value,
                            esLogin = esLogin
                        ),
                        error
                    )
                }
            }
        }

    }

    fun getBaseUrl(): String = BASE_URL
}
