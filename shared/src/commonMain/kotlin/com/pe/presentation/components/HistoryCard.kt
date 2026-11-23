
package com.pe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable

import com.pe.domain.model.Historial
import com.pe.presentation.theme.*

@Composable
fun HistoryCard(
    historial: Historial,
    modifier: Modifier = Modifier,
    onVerDetalle: ((Historial) -> Unit)? = null
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
    val detalle = when (historial.tipoAccion) {
        "SOLICITUD_CREADA" ->
            "Se registró una nueva solicitud de préstamo de equipos."

        "SOLICITUD_APROBADA" ->
            "La solicitud de préstamo fue aprobada."

        "SOLICITUD_RECHAZADA" ->
            "La solicitud de préstamo fue rechazada."

        "SOLICITUD_CANCELADA" ->
            "La solicitud de préstamo fue cancelada."

        "PRESTAMO_INICIADO" ->
            "Se inició el préstamo de equipos."

        "PRESTAMO_CANCELADO" ->
            "El préstamo de equipos fue cancelado."

        "DEVOLUCION_REGISTRADA" ->
            "Se registró la devolución de los equipos prestados."

        "INCIDENCIA_REPORTADA" ->
            "Se registró una nueva incidencia en el laboratorio."

        "INCIDENCIA_EN_PROCESO" ->
            "El encargado comenzó a atender la incidencia."

        "INCIDENCIA_RESUELTA" ->
            "La incidencia fue marcada como resuelta."

        else -> historial.descripcion
            ?.takeIf { it.isNotBlank() }
            ?: "Se registró una actividad."
    }

    // Categoría del registro
    val categoria = when (historial.tipoEntidad) {
        "SOLICITUD" -> "Solicitud"
        "PRESTAMO" -> "Préstamo"
        "INCIDENCIA" -> "Incidencia"
        else -> historial.tipoEntidad
    }


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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = titulo,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = historial.codigoEntidad?.takeIf { it.isNotBlank() } ?: categoria,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // DESCRIPCIÓN
            Text(
                text = detalle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody
            )

            // FECHA Y HORA
            Text(
                text = historial.fechaAccionVisual,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            HorizontalDivider(color = Divider)

            // ACCESO AL DETALLE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = onVerDetalle != null) {
                        onVerDetalle?.invoke(historial)
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ver información completa",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBody
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextMuted
                )
            }
        }
    }
}
