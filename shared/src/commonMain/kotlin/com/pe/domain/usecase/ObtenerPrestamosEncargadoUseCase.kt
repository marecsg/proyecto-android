package com.pe.domain.usecase

import com.pe.domain.model.LabSession
import com.pe.domain.repository.PrestamoRepository

class ObtenerPrestamosEncargadoUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(session: LabSession): List<com.pe.domain.model.Loan> {
        require(session.esEncargado) { "Acceso exclusivo para encargados" }
        return repository.obtenerDelEncargado(session)
    }
}
