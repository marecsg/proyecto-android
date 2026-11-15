package com.pe.domain.usecase.Prestamo

import com.pe.domain.model.Prestamo
import com.pe.domain.repository.PrestamoRepository

class ObtenerPrestamosAmbienteUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(idAmbiente: Int): Result<List<Prestamo>> {
        return repository.obtenerPorAmbiente(idAmbiente)
    }
}
