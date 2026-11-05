package com.pe.domain.model

data class LabSession(
    val id: Int,
    val nombre: String,
    val correo: String,
    val rol: String,
    val token: String
) {
    val esEncargado: Boolean get() = rol.equals("Encargado", ignoreCase = true)
}
