package com.pe.domain.usecase.Prestamo

import com.pe.domain.model.Prestamo
import com.pe.domain.repository.PrestamoRepository

class ObtenerPrestamosEstudianteUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(idEstudiante: Int): Result<List<Prestamo>> {
        return repository.obtenerPorEstudiante(idEstudiante)
    }
}
