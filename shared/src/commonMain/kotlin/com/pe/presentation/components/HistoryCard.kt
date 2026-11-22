
package com.pe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Historial
import com.pe.presentation.theme.*

@Composable
fun HistoryCard(
    historial: Historial,
    modifier: Modifier = Modifier
) {

    // Convertir tipo de acción en texto amigable
    val titulo = when (historial.tipoAccion) {
        "SOLICITUD_CREADA" -> "Solicitud creada"
        "SOLICITUD_APROBADA" -> "Solicitud aprobada"
        "SOLICITUD_RECHAZADA" -> "Solicitud rechazada"
        "SOLICITUD_CANCELADA" -> "Solicitud cancelada"

        "PRESTAMO_INICIADO" -> "Préstamo iniciado"
        "PRESTAMO_CANCELADO" -> "Préstamo cancelado"
        "DEVOLUCION_REGISTRADA" -> "Devolución registrada"

        "INCIDENCIA_REPORTADA" -> "Incidencia reportada"
        "INCIDENCIA_EN_PROCESO" -> "Incidencia en proceso"
        "INCIDENCIA_RESUELTA" -> "Incidencia resuelta"

        else -> historial.tipoAccion.replace("_", " ")
    }

    // Colores según la acción
    val tono = when (historial.tipoAccion) {
        "SOLICITUD_APROBADA",
        "DEVOLUCION_REGISTRADA",
        "INCIDENCIA_RESUELTA" -> TonoEstado.SUCCESS

        "SOLICITUD_RECHAZADA",
        "SOLICITUD_CANCELADA",
        "PRESTAMO_CANCELADO" -> TonoEstado.ERROR

        "SOLICITUD_CREADA",
        "INCIDENCIA_REPORTADA" -> TonoEstado.WARNING

        else -> TonoEstado.INFO
    }

    // Categoría del registro
    val categoria = when (historial.tipoEntidad) {
        "SOLICITUD" -> "Solicitud"
        "PRESTAMO" -> "Préstamo"
        "INCIDENCIA" -> "Incidencia"
        else -> historial.tipoEntidad
    }

    // Fecha enviada por Spring Boot:
    // 2026-11-19T11:20:35.352344
    val fechaOriginal = historial.fechaAccion
        .substringBefore("T")

    val partes = fechaOriginal.split("-")

    val fecha = if (partes.size == 3) {
        "${partes[2]}/${partes[1]}/${partes[0]}"
    } else {
        fechaOriginal
    }

    val hora = historial.fechaAccion
        .substringAfter("T", "")
        .take(5)
        .ifBlank { "--:--" }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        border = BorderStroke(1.dp, Border)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // ENCABEZADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimarySoft
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark
                    )

                    Text(
                        text = categoria,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                BadgeEstado(
                    label = "Actividad",
                    tone = tono
                )
            }

            // DESCRIPCIÓN
            if (!historial.descripcion.isNullOrBlank()) {
                Text(
                    text = historial.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBody
                )
            }

            HorizontalDivider(color = Divider)

            // RESPONSABLE DE LA ACCIÓN
            Text(
                text = "Responsable: ${historial.nombreActor}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
            )

            // MOSTRAR AFECTADO SOLO SI ES OTRA PERSONA
            if (
                !historial.actorEsAfectado &&
                historial.nombreAfectado.isNotBlank()
            ) {
                Text(
                    text = "Estudiante relacionado: " +
                            historial.nombreAfectado,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBody
                )
            }

            // FECHA Y HORA
            Text(
                text = "$fecha · $hora",
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted
            )
        }
    }
}
