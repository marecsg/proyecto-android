package com.pe.domain.usecase

import com.pe.domain.model.Loan
import com.pe.domain.model.Usuario
import com.pe.domain.repository.PrestamoRepository

class ResponderPrestamoUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(usuario: Usuario, idSolicitud: Int, aceptar: Boolean): Result<Loan> {
        return repository.responder(usuario, idSolicitud, aceptar)
    }
}