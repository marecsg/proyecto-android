package com.pe.data.remote.api

import io.ktor.client.HttpClient
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
        }
    }

    fun getBaseUrl(): String = BASE_URL
}
