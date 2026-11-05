package com.pe.domain.usecase

import com.pe.domain.model.LabSession
import com.pe.domain.repository.PrestamoRepository

class ResponderPrestamoUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(session: LabSession, idSolicitud: Int, aceptar: Boolean): com.pe.domain.model.Loan {
        require(session.esEncargado && idSolicitud > 0) { "Operación exclusiva del encargado" }
        return repository.responder(session, idSolicitud, aceptar)
    }
}
