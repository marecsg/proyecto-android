package com.pe.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

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

    fun convertirHora24(hora: String, periodo: String): String? {

        val digitos = hora.filter { it.isDigit() }

        val horaFormateada = when (digitos.length) {
            1, 2 -> {
                "${digitos.padStart(2, '0')}:00"
            }
            3 -> {
                "0${digitos[0]}:${digitos.substring(1)}"
            }
            4 -> {
                "${digitos.substring(0, 2)}:${digitos.substring(2)}"
            }
            else -> return null
        }

        val partes = horaFormateada.split(":")
        val horas = partes[0].toIntOrNull() ?: return null
        val minutos = partes[1].toIntOrNull() ?: return null

        if (horas !in 1..12 || minutos !in 0..59) {
            return null
        }

        if (periodo != "AM" && periodo != "PM") return null

        val hora24 = (horas % 12) +
                if (periodo == "PM") 12 else 0

        return "${hora24.toString().padStart(2, '0')}:" +
                minutos.toString().padStart(2, '0')
    }

}

object HoraVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digitos = text.text

        val horaVisible = when (digitos.length) {
            0 -> ""
            1 -> "0$digitos:00"
            2 -> {
                if ((digitos.toIntOrNull() ?: 0) in 1..12) {
                    "$digitos:00"
                } else {
                    "0${digitos[0]}:0${digitos[1]}"
                }
            }
            3 -> "0${digitos[0]}:${digitos.substring(1)}"
            4 -> "${digitos.substring(0, 2)}:${digitos.substring(2)}"
            else -> digitos
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset == 0) 0 else horaVisible.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (offset == 0) 0 else digitos.length
            }
        }

        return TransformedText(
            AnnotatedString(horaVisible),
            mapping
        )
    }
}