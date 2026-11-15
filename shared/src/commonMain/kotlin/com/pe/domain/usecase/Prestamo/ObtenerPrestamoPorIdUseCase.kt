package com.pe.domain.usecase.Prestamo

import com.pe.domain.model.Prestamo
import com.pe.domain.repository.PrestamoRepository

class ObtenerPrestamoPorIdUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(idPrestamo: Int): Result<Prestamo> {
        return repository.obtenerPrestamoPorId(idPrestamo)
    }
}