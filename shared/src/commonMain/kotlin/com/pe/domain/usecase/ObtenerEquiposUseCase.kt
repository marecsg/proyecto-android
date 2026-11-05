package com.pe.domain.usecase

import com.pe.domain.model.LabSession
import com.pe.domain.repository.EquipoRepository

class ObtenerEquiposUseCase(private val repository: EquipoRepository) {
    suspend operator fun invoke(session: LabSession) = repository.obtenerEquipos(session)
}
