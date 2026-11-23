
package com.pe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.Prestamo
import com.pe.presentation.theme.*

@Composable
fun PrestamoCard(
    prestamo: Prestamo,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    esEncargado: Boolean = false,
    onIniciarPrestamo: (Int) -> Unit = {},
    onRegistrarDevolucion: (Int) -> Unit = {}
) {
    val resumenActividad = when (prestamo.estado) {
        EstadoPrestamo.PENDIENTE ->
            "Pendiente de entrega"

        EstadoPrestamo.EN_CURSO,
        EstadoPrestamo.VENCIDO ->
            if (!prestamo.fechaInicioUso.isNullOrBlank()) {
                "Inicio real: ${prestamo.fechaInicoVisual}"
            } else {
                "Inicio real: Sin registrar"
            }

        EstadoPrestamo.FINALIZADO ->
            if (!prestamo.fechaFinUso.isNullOrBlank()) {
                "Devolución: ${prestamo.fechaFinVisual}"
            } else {
                "Devolución: Sin registrar"
            }

        EstadoPrestamo.CANCELADO -> null
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
            modifier = Modifier.fillMaxWidth()
        ) {

            // ZONA PULSABLE: ABRIR DETALLE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(prestamo.idPrestamo) }
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // CÓDIGO Y ESTADO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = prestamo.codigoCorrelativo
                            ?.takeIf { it.isNotBlank() } ?: "----",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    BadgeEstado(
                        label = prestamo.estado.etiqueta,
                        tone = prestamo.estado.toTono()
                    )
                }

                // ESTUDIANTE, SOLO ENCARGADO
                if (esEncargado) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Column {
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

                HorizontalDivider(color = Divider)

                // EQUIPOS
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
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Ver detalle",
                        tint = TextMuted
                    )
                }

                // ÚNICA FECHA RELEVANTE EN EL RESUMEN
                if (resumenActividad != null) {
                    HorizontalDivider(color = Divider)

                    Text(
                        text = resumenActividad,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextBody
                    )
                }
            }

            // BOTONES DEL ENCARGADO
            // Separados de la zona pulsable del detalle
            if (esEncargado &&
                prestamo.estado in listOf(
                    EstadoPrestamo.PENDIENTE,
                    EstadoPrestamo.EN_CURSO
                )
            ) {
                HorizontalDivider(color = Divider)

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
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
}
