
package com.pe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Solicitud
import com.pe.presentation.theme.*

@Composable
fun SolicitudCard(
    solicitud: Solicitud,
    numero: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    mostrarEstudiante: Boolean = false,
    codigoEstudiante: String? = null

) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(solicitud.idSolicitud)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Solicitud N.º ${numeroVisual(numero)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark
                    )

                    Text(
                        text = solicitud.nombreAmbiente,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                BadgeEstado(
                    label = solicitud.estado.etiqueta,
                    tone = solicitud.estado.toTono()
                )
            }

            if (mostrarEstudiante) {
                Column {
                    Text(
                        text = solicitud.nombreEstudiante,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark
                    )

                    if (!codigoEstudiante.isNullOrBlank()) {
                        Text(
                            text = "Cód. $codigoEstudiante",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            Text(
                text = when (solicitud.detalles.size) {
                    0 -> "Sin equipos"
                    1 -> solicitud.detalles.first().nombreEquipo
                    else -> "${solicitud.detalles.first().nombreEquipo} " +
                            "(+${solicitud.detalles.size - 1} más)"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
            )

            Text(
                text = solicitud.motivoSolicitud,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 2
            )

            Text(
                text = "Inicio: ${solicitud.fechaInicioReserva}",
                style = MaterialTheme.typography.labelSmall,
                color = TextBody
            )

            Text(
                text = "Fin: ${solicitud.fechaFinReserva}",
                style = MaterialTheme.typography.labelSmall,
                color = TextBody
            )

            Text(
                text = "Ver detalle",
                style = MaterialTheme.typography.labelMedium,
                color = Primary
            )
        }
    }
}
