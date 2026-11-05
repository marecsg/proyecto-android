package com.pe.domain.repository

import com.pe.domain.model.Equipment
import com.pe.domain.model.LabSession

interface EquipoRepository {
    suspend fun obtenerEquipos(session: LabSession): List<Equipment>
}
