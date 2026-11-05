package com.pe.data.remote.mapper

import com.pe.data.remote.dto.PrestamoDto
import com.pe.domain.model.Equipment
import com.pe.domain.model.EquipmentStatus
import com.pe.domain.model.Loan
import com.pe.domain.model.LoanStatus

/** El backend almacena Curso/Docente/Motivo en un solo campo de texto. */
private fun leerParte(motivo: String, campo: String): String? =
    motivo.split(';').firstOrNull { it.trim().startsWith("$campo:", ignoreCase = true) }
        ?.substringAfter(':')?.trim()?.takeIf { it.isNotBlank() }

private fun fechaVisual(iso: String): String {
    val parts = iso.substringBefore('T').split('-')
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else iso
}

fun PrestamoDto.toDomain(): Loan {
    val equipo = equipo?.toDomain() ?: Equipment(
        id = "", name = "Equipo", category = "", laboratory = "",
        status = EquipmentStatus.LOANED, description = ""
    )
    val status = when (estadoSolicitud) {
        "ACEPTADA" -> LoanStatus.APPROVED
        "RECHAZADA" -> LoanStatus.REJECTED
        "EN_CURSO" -> LoanStatus.IN_USE
        "FINALIZADO" -> LoanStatus.RETURNED
        else -> LoanStatus.PENDING
    }
    return Loan(
        id = "PRE-$idSolicitud",
        equipment = equipo,
        requester = listOf(estudiante?.nombres.orEmpty(), estudiante?.apellidos.orEmpty())
            .filter { it.isNotBlank() }.joinToString(" "),
        course = leerParte(motivoSolicitud, "Curso") ?: "No especificado",
        teacher = leerParte(motivoSolicitud, "Docente") ?: "No especificado",
        date = fechaVisual(fechaInicioUso),
        startTime = fechaInicioUso.substringAfter('T', "--:--").take(5),
        endTime = fechaFinUso.substringAfter('T', "--:--").take(5),
        reason = motivoSolicitud,
        status = status,
        remoteId = idSolicitud,
        returnDate = fechaVisual(fechaFinUso)
    )
}
