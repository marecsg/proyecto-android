
package com.pe.presentation.screens.prestamos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pe.domain.model.EstadoItem
import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.Prestamo
import com.pe.domain.model.Solicitud
import com.pe.presentation.components.BadgeEstado
import com.pe.presentation.components.BotonPrincipal
import com.pe.presentation.components.toTono
import com.pe.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePrestamoContent(
    prestamo: Prestamo,
    esEncargado: Boolean,
    onVolver: () -> Unit,
    onCancelarPrestamo: (Int) -> Unit,
    onIniciarPrestamo: (Int) -> Unit,
    onRegistrarDevolucion: (Int) -> Unit,
    modifier: Modifier = Modifier,
    solicitud: Solicitud? = null,
    procesando: Boolean = false,
    error: String? = null
) {
    var confirmarCancelacion by remember(prestamo.idPrestamo) {
        mutableStateOf(false)
    }

    var confirmarInicio by remember(prestamo.idPrestamo) {
        mutableStateOf(false)
    }

    // Confirmar cancelación del estudiante
    if (confirmarCancelacion) {
        AlertDialog(
            onDismissRequest = {
                confirmarCancelacion = false
            },
            title = {
                Text("Cancelar préstamo")
            },
            text = {
                Text(
                    "¿Seguro que deseas cancelar este préstamo pendiente?"
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        confirmarCancelacion = false
                        onCancelarPrestamo(prestamo.idPrestamo)
                    }
                ) {
                    Text("Sí, cancelar", color = ErrorText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmarCancelacion = false }
                ) {
                    Text("Volver")
                }
            }
        )
    }

    // Confirmar inicio del préstamo
    if (confirmarInicio) {
        AlertDialog(
            onDismissRequest = {
                confirmarInicio = false
            },
            title = {
                Text("Iniciar préstamo")
            },
            text = {
                Text(
                    "¿Confirmas que los equipos fueron entregados al estudiante?"
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        confirmarInicio = false
                        onIniciarPrestamo(prestamo.idPrestamo)
                    }
                ) {
                    Text("Sí, iniciar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmarInicio = false }
                ) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundClean,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del préstamo",
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundClean
                )
            )
        },
        bottomBar = {
            val accionVisible = when {
                esEncargado &&
                    prestamo.estado == EstadoPrestamo.PENDIENTE ->
                    "Iniciar préstamo"

                esEncargado &&
                    prestamo.estado == EstadoPrestamo.EN_CURSO ->
                    "Registrar devolución"

                !esEncargado &&
                    prestamo.estado == EstadoPrestamo.PENDIENTE ->
                    "Cancelar préstamo"

                else -> null
            }

            if (accionVisible != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite)
                        .padding(16.dp)
                ) {
                    BotonPrincipal(
                        texto = if (procesando) {
                            "Procesando..."
                        } else {
                            accionVisible
                        },
                        habilitado = !procesando,
                        secundario = !esEncargado,
                        onClick = {
                            when {
                                esEncargado &&
                                    prestamo.estado == EstadoPrestamo.PENDIENTE -> {
                                    confirmarInicio = true
                                }

                                esEncargado &&
                                    prestamo.estado == EstadoPrestamo.EN_CURSO -> {
                                    onRegistrarDevolucion(prestamo.idPrestamo)
                                }

                                !esEncargado &&
                                    prestamo.estado == EstadoPrestamo.PENDIENTE -> {
                                    confirmarCancelacion = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // INFORMACIÓN DEL ESTUDIANTE
            if (esEncargado) {
                SeccionPrestamo {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Primary
                        )

                        Column {
                            Text(
                                prestamo.nombreEstudiante,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )

                            if (prestamo.codigoEstudiante.isNotBlank()) {
                                Text(
                                    "Cód. ${prestamo.codigoEstudiante}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // INFORMACIÓN GENERAL DEL PRÉSTAMO
            SeccionPrestamo {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "PRÉSTAMO DE EQUIPOS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )

                        Text(
                            text = prestamo.codigoCorrelativo
                                ?.takeIf { it.isNotBlank() } ?: "----",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    BadgeEstado(
                        label = prestamo.estado.etiqueta,
                        tone = prestamo.estado.toTono()
                    )
                }

                Text(
                    text = prestamo.nombreAmbiente,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )

                if (solicitud != null) {
                    HorizontalDivider(color = Divider)

                    Text(
                        text = "Período programado",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                    )

                    TextoDato(
                        etiqueta = "Retiro previsto",
                        valor = solicitud.inicioReservaVisual
                    )

                    TextoDato(
                        etiqueta = "Devolución prevista",
                        valor = solicitud.finReservaVisual
                    )
                }
            }

            // EQUIPOS DEL PRÉSTAMO
            SeccionPrestamo {
                Text(
                    "Equipos (${prestamo.detalles.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                prestamo.detalles.forEachIndexed { index, detalle ->
                    if (index > 0) {
                        HorizontalDivider(color = Divider)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                detalle.nombreEquipo,
                                color = TextDark,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                detalle.codigoQrEquipo,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }

                        Text(
                            detalle.estadoItem?.etiqueta
                                ?: when (prestamo.estado) {
                                    EstadoPrestamo.PENDIENTE ->
                                        "Por entregar"

                                    EstadoPrestamo.EN_CURSO,
                                    EstadoPrestamo.VENCIDO ->
                                        "En préstamo"

                                    else -> "Sin evaluación"
                                },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (
                                detalle.estadoItem == EstadoItem.NO_DEVUELTO
                            ) {
                                ErrorText
                            } else {
                                Primary
                            }
                        )
                    }

                    // Solo muestra los resultados de una devolución
                    // que ya fue registrada.
                    if (detalle.estadoFisicoAlDevolver != null) {
                        TextoDato(
                            "Estado físico",
                            detalle.estadoFisicoAlDevolver
                        )
                    }

                    if (!detalle.comentarioDevolucion.isNullOrBlank()) {
                        TextoDato(
                            "Observación",
                            detalle.comentarioDevolucion
                        )
                    }
                }
            }

            // MOTIVO ORIGINAL
            if (solicitud != null) {
                SeccionPrestamo {
                    Text(
                        "Motivo",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted
                    )

                    Text(
                        solicitud.motivoSolicitud,
                        color = TextBody
                    )
                }
            }

            // SEGUIMIENTO
            SeccionPrestamo {
                Text(
                    "Seguimiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                if (solicitud != null) {
                    HitoPrestamo(
                        "Solicitud enviada",
                        solicitud.fechaSolicitudVisual,
                        true
                    )

                    HitoPrestamo(
                        "Solicitud aprobada",
                        solicitud.fechaRespuesta
                            ?.let { solicitud.fechaRespuestaVisual },
                        true
                    )
                }

                when (prestamo.estado) {
                    EstadoPrestamo.PENDIENTE -> {
                        HitoPrestamo(
                            "Entrega pendiente",
                            null,
                            false
                        )
                    }

                    EstadoPrestamo.EN_CURSO,
                    EstadoPrestamo.VENCIDO -> {
                        HitoPrestamo(
                            "Equipos retirados",
                            prestamo.fechaInicoVisual,
                            true
                        )

                        HitoPrestamo(
                            "Devolución pendiente",
                            null,
                            false
                        )
                    }

                    EstadoPrestamo.FINALIZADO -> {
                        HitoPrestamo(
                            "Equipos retirados",
                            prestamo.fechaInicoVisual,
                            true
                        )

                        HitoPrestamo(
                            "Devolución registrada",
                            prestamo.fechaFinVisual,
                            true
                        )
                    }

                    EstadoPrestamo.CANCELADO -> {
                        HitoPrestamo(
                            "Préstamo cancelado",
                            null,
                            true
                        )
                    }
                }
            }

            if (error != null) {
                Text(
                    error,
                    color = ErrorText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SeccionPrestamo(
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = contenido
        )
    }
}

@Composable
private fun TextoDato(
    etiqueta: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.weight(1f)
        )

        Text(
            valor,
            style = MaterialTheme.typography.bodySmall,
            color = TextBody,
            modifier = Modifier.weight(1.6f)
        )
    }
}

@Composable
private fun HitoPrestamo(
    titulo: String,
    fecha: String?,
    completado: Boolean
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = if (completado) {
                Icons.Default.CheckCircle
            } else {
                Icons.Default.RadioButtonUnchecked
            },
            contentDescription = null,
            tint = if (completado) Primary else BorderStrong,
            modifier = Modifier.size(18.dp)
        )

        Column {
            Text(
                titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )

            if (!fecha.isNullOrBlank()) {
                Text(
                    fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}
