package com.pe.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    val token: String
)
