package com.pe.domain.usecase

import com.pe.domain.model.*
import com.pe.domain.repository.PrestamoRepository
import kotlinx.datetime.LocalDate

/** Validaciones del préstamo independientes de Ktor y Compose. */
class SolicitarPrestamoUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(session: LabSession, equipo: Equipment, draft: LoanDraft): Loan {
        require(!session.esEncargado) { "Utiliza una cuenta de estudiante para solicitar" }
        require(equipo.remoteId > 0) { "El equipo no existe en el servidor" }
        val inicio = convertirFecha(draft.date) + "T" + validarHora(draft.startTime)
        val fin = convertirFecha(draft.returnDate) + "T" + validarHora(draft.endTime)
        require(fin > inicio) { "La devolución debe ser posterior a la recogida" }
        require(draft.course.isNotBlank() && draft.teacher.isNotBlank() && draft.reason.isNotBlank()) {
            "Completa curso, docente y motivo"
        }
        return repository.solicitar(session, equipo, draft)
    }
}

internal fun convertirFecha(fecha: String): String {
    val partes = fecha.trim().split('/')
    require(partes.size == 3) { "Fecha inválida: utiliza dd/MM/aaaa" }
    val date = runCatching { LocalDate(partes[2].toInt(), partes[1].toInt(), partes[0].toInt()) }
        .getOrNull() ?: throw IllegalArgumentException("Fecha inválida: utiliza dd/MM/aaaa")
    return date.toString()
}

internal fun validarHora(hora: String): String {
    require(Regex("[0-9]{2}:[0-9]{2}").matches(hora)) { "Utiliza el formato de hora HH:mm" }
    val h = hora.take(2).toInt()
    val m = hora.takeLast(2).toInt()
    require(h in 0..23 && m in 0..59) { "La hora no es válida" }
    return "$hora:00"
}
