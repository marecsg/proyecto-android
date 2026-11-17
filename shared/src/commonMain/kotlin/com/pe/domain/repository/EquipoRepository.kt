package com.pe.domain.repository

import com.pe.domain.model.Equipo
import com.pe.domain.model.Usuario

interface EquipoRepository {
    suspend fun obtenerEquipos(): Result<List<Equipo>>
    suspend fun obtenerEquipoPorId(idEquipo: Int): Result<Equipo>
    suspend fun obtenerEquipoPorQr(codigoQr: String): Result<Equipo>
}