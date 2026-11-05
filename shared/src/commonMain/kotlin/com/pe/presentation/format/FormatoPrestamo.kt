package com.pe.presentation.format

import com.pe.domain.model.Loan

// Horarios legibles para el usuario, conservando internamente HH:mm de 24 horas.
fun horaLegible(value: String): String {
    val parts = value.split(":")
    val hour = parts.firstOrNull()?.toIntOrNull() ?: return value
    val min = parts.getOrNull(1)?.toIntOrNull() ?: return value
    if (hour !in 0..23 || min !in 0..59) return value
    val h12 = when (val h = hour % 12) { 0 -> 12; else -> h }
    val period = if (hour < 12) "AM" else "PM"
    return "$h12:${min.toString().padStart(2, '0')} $period"
}

fun Loan.rangoLegible(): String =
    "${date} ${horaLegible(startTime)} → ${returnDate} ${horaLegible(endTime)}"
