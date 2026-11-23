package com.pe.utils

object FechaUtils {

    fun formatearFecha(fechaIso: String?): String {
        if (fechaIso.isNullOrBlank()) return "No disponible"

        val partes = fechaIso.substringBefore("T").split("-")

        if (partes.size != 3) return fechaIso

        return "${partes[2]}/${partes[1]}/${partes[0]}"
    }

    fun formatearFechaHora(fechaIso: String?): String {
        if (fechaIso.isNullOrBlank()) return "No disponible"

        val fecha = formatearFecha(fechaIso)
        val horaTexto = fechaIso.substringAfter("T", "")

        if (horaTexto.isBlank()) return fecha

        val partesHora = horaTexto.split(":")

        val hora24 = partesHora.getOrNull(0)?.toIntOrNull()
            ?: return fecha

        val minutos = partesHora.getOrNull(1)?.toIntOrNull()
            ?: return fecha

        if (hora24 !in 0..23 || minutos !in 0..59) {
            return fecha
        }

        val hora12 = if (hora24 % 12 == 0) 12 else hora24 % 12
        val periodo = if (hora24 < 12) "AM" else "PM"

        return "$fecha · $hora12:${minutos.toString().padStart(2, '0')} $periodo"
    }

    fun formatearHora(fechaIso: String?): String {
        if (fechaIso.isNullOrBlank()) return "--:--"

        val horaTexto = fechaIso.substringAfter("T", "")
        val partes = horaTexto.split(":")

        val hora24 = partes.getOrNull(0)?.toIntOrNull() ?: return "--:--"
        val minutos = partes.getOrNull(1)?.toIntOrNull() ?: return "--:--"

        if (hora24 !in 0..23 || minutos !in 0..59) return "--:--"

        val hora12 = if (hora24 % 12 == 0) 12 else hora24 % 12
        val periodo = if (hora24 < 12) "AM" else "PM"

        return "$hora12:${minutos.toString().padStart(2, '0')} $periodo"
    }

    fun convertirHora24(hora: String, periodo: String): String? {
        if (!Regex("""(0[1-9]|1[0-2]):[0-5][0-9]""").matches(hora)) {
            return null
        }

        val (horas, minutos) = hora.split(":")
        val hora12 = horas.toInt()

        val hora24 = (hora12 % 12) +
                if (periodo == "PM") 12 else 0

        return "${hora24.toString().padStart(2, '0')}:$minutos"
    }

}
