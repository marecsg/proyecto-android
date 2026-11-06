package com.pe.domain.usecase

import com.pe.domain.model.*
import com.pe.domain.repository.PrestamoRepository

class SolicitarPrestamoUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(usuario: Usuario, equipo: Equipment, draft: LoanDraft): Result<Loan> {
        return repository.solicitar(usuario, equipo, draft)
    }
}