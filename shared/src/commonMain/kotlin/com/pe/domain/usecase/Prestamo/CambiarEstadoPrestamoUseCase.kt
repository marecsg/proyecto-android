package com.pe.domain.usecase.Prestamo

import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.Prestamo
import com.pe.domain.repository.PrestamoRepository

class CambiarEstadoPrestamoUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(idPrestamo: Int, nuevoEstado: EstadoPrestamo): Result<Prestamo> {
        return repository.cambiarEstado(idPrestamo, nuevoEstado)
    }
}