
package com.pe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Prestamo
import com.pe.domain.model.EstadoPrestamo
import com.pe.presentation.theme.*

@Composable
fun PrestamoCard(
    prestamo: Prestamo,
    numero: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    esEncargado: Boolean = false,
    onIniciarPrestamo: (Int) -> Unit = {},
    onRegistrarDevolucion: (Int) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(prestamo.idPrestamo)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // NÚMERO VISUAL Y ESTADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Préstamo N.º ${numeroVisual(numero)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                BadgeEstado(
                    label = prestamo.estado.etiqueta,
                    tone = prestamo.estado.toTono()
                )
            }

            // ESTUDIANTE: SOLO VISIBLE PARA ENCARGADO
            if (esEncargado) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                PrimaryContainer,
                                RoundedCornerShape(50.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Primary
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = prestamo.nombreEstudiante,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDark
                        )

                        if (prestamo.codigoEstudiante.isNotBlank()) {
                            Text(
                                text = "Cód. ${prestamo.codigoEstudiante}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            // RESUMEN DE EQUIPOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            PrimarySoft,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = null,
                        tint = Primary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = prestamo.resumenEquipos,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${prestamo.detalles.size} equipo(s) · " +
                                prestamo.nombreAmbiente,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextMuted
                )
            }

            HorizontalDivider(color = Divider)

            // FECHAS REALES DEL PRÉSTAMO
            when (prestamo.estado) {

                EstadoPrestamo.PENDIENTE -> {
                    Text(
                        text = "Pendiente de entrega",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }

                EstadoPrestamo.EN_CURSO,
                EstadoPrestamo.VENCIDO -> {
                    Text(
                        text = if (prestamo.fechaInicioUso.isNullOrBlank()) {
                            "Inicio real: ${prestamo.fechaVisual} · " +
                                    prestamo.horaInicio
                        } else {
                            "Inicio real: Desconocido"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextBody
                    )
                }

                EstadoPrestamo.FINALIZADO -> {
                    Text(
                        text = "Horario real: ${prestamo.horarioPrestamo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextBody
                    )
                }

                EstadoPrestamo.CANCELADO -> {
                    Text(
                        text = "Préstamo cancelado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            Text(
                text = "Ver detalle",
                style = MaterialTheme.typography.labelMedium,
                color = Primary
            )

            // ACCIONES DEL ENCARGADO
            if (esEncargado) {
                when (prestamo.estado) {

                    EstadoPrestamo.PENDIENTE -> {
                        BotonPrincipal(
                            texto = "Iniciar préstamo",
                            onClick = {
                                onIniciarPrestamo(prestamo.idPrestamo)
                            }
                        )
                    }

                    EstadoPrestamo.EN_CURSO -> {
                        BotonPrincipal(
                            texto = "Registrar devolución",
                            onClick = {
                                onRegistrarDevolucion(prestamo.idPrestamo)
                            }
                        )
                    }

                    else -> Unit
                }
            }
        }
    }
}
