
package com.pe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Solicitud
import com.pe.presentation.theme.*

@Composable
fun SolicitudCard(
    solicitud: Solicitud,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    mostrarEstudiante: Boolean = false,
    codigoEstudiante: String? = null
) {

    val resumenEquipos = when (solicitud.detalles.size) {
        0 -> "Sin equipos"
        1 -> solicitud.detalles.first().nombreEquipo
        else -> "${solicitud.detalles.first().nombreEquipo} " +
                "(+${solicitud.detalles.size - 1} más)"
    }

    val codigoAlumno = codigoEstudiante
        ?.takeIf { it.isNotBlank() }
        ?: solicitud.codigoEstudiante.takeIf { it.isNotBlank() }

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // IDENTIFICACIÓN Y ESTADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = solicitud.codigoCorrelativo
                            ?.takeIf { it.isNotBlank() } ?: "----",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )

                    Text(
                        text = solicitud.nombreAmbiente,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                BadgeEstado(
                    label = solicitud.estado.etiqueta,
                    tone = solicitud.estado.toTono()
                )
            }

            // ESTUDIANTE: SOLO PARA ENCARGADOS
            if (mostrarEstudiante) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = solicitud.nombreEstudiante,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark
                    )

                    if (codigoAlumno != null) {
                        Text(
                            text = "Cód. $codigoAlumno",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            HorizontalDivider(color = Divider)

            // RESUMEN DE EQUIPOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Devices,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = resumenEquipos,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${solicitud.detalles.size} equipo(s)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver detalle",
                    tint = TextMuted
                )
            }

            HorizontalDivider(color = Divider)

            // PERÍODO DE RESERVA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Retiro previsto",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    Text(
                        text = solicitud.inicioReservaVisual,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark,
                        maxLines = 2
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Devolución prevista",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    Text(
                        text = solicitud.finReservaVisual,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
