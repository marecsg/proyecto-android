package com.pe.domain.repository

import com.pe.domain.model.Equipment
import com.pe.domain.model.Usuario

interface EquipoRepository {
    suspend fun obtenerEquipos(): Result<List<Equipment>>
}