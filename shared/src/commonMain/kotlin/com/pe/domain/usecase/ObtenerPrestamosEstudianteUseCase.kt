package com.pe.domain.usecase

import com.pe.domain.model.Loan
import com.pe.domain.model.Usuario
import com.pe.domain.repository.PrestamoRepository

class ObtenerPrestamosEstudianteUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(usuario: Usuario): Result<List<Loan>> {
        return repository.obtenerDelEstudiante(usuario)
    }
}
