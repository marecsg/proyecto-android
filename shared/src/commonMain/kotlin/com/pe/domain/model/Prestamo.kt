package com.pe.domain.model

enum class LoanStatus(val label: String) {
    PENDING("Pendiente"),
    APPROVED("Aprobado"),
    IN_USE("En uso"),
    RETURNED("Devuelto"),
    REJECTED("Rechazado"),
    CANCELLED("Cancelado")
}

data class LoanDraft(
    val course: String,
    val teacher: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val reason: String,
    val returnDate: String = date
)

data class Loan(
    val id: String,
    val equipment: Equipment,
    val requester: String,
    val course: String,
    val teacher: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val reason: String,
    val status: LoanStatus,
    val remoteId: Int = 0,
    val returnDate: String = date
)
