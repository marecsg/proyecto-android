package com.pe.domain.repository

import com.pe.domain.model.Equipment
import com.pe.domain.model.LabSession
import com.pe.domain.model.Loan
import com.pe.domain.model.LoanDraft

interface PrestamoRepository {
    suspend fun obtenerDelEstudiante(session: LabSession): List<Loan>
    suspend fun obtenerDelEncargado(session: LabSession): List<Loan>
    suspend fun solicitar(session: LabSession, equipo: Equipment, draft: LoanDraft): Loan
    suspend fun responder(session: LabSession, idSolicitud: Int, aceptar: Boolean): Loan
}
