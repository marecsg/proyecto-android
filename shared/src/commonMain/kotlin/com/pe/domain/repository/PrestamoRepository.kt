package com.pe.domain.repository

import com.pe.domain.model.Equipment
import com.pe.domain.model.Loan
import com.pe.domain.model.LoanDraft
import com.pe.domain.model.Usuario

interface PrestamoRepository {
    suspend fun obtenerDelEstudiante(usuario: Usuario): Result<List<Loan>>
    suspend fun obtenerDelEncargado(usuario: Usuario): Result<List<Loan>>
    suspend fun solicitar(usuario: Usuario, equipo: Equipment, draft: LoanDraft): Result<Loan>
    suspend fun responder(usuario: Usuario, idSolicitud: Int, aceptar: Boolean): Result<Loan>
}
