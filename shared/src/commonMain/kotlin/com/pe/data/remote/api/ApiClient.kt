package com.pe.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * La URL que solicitó el profesor está aquí.
 * 10.0.2.2 = localhost de la computadora DESDE el emulador Android.
 * Si se prueba en un celular real, sustituir por la IPv4 del PC en la misma red.
 */
object ApiClient {
    const val BASE_URL = "http://10.0.2.2:8080"

    val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            expectSuccess = true
        }
    }

    fun getBaseUrl(): String = BASE_URL
}
