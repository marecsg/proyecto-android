package com.pe.domain.usecase

import com.pe.domain.model.LabSession
import com.pe.domain.repository.PrestamoRepository

class ObtenerPrestamosEstudianteUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(session: LabSession) = repository.obtenerDelEstudiante(session)
}
