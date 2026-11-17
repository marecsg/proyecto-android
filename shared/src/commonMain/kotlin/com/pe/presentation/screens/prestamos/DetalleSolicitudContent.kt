package com.pe.presentation.screens.solicitudes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pe.domain.model.EstadoSolicitud
import com.pe.domain.model.Solicitud
import com.pe.presentation.components.BadgeEstado
import com.pe.presentation.components.numeroVisual
import com.pe.presentation.components.toTono
import com.pe.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSolicitudContent(
    solicitud: Solicitud,
    numero: Int,
    esEncargado: Boolean,
    onBack: () -> Unit,
    onCancelar: (Int) -> Unit,
    onAprobar: (Int, String?) -> Unit,
    onRechazar: (Int, String?) -> Unit,
    onVerPrestamo: (Int) -> Unit,
    onVolverASolicitar: () -> Unit,
    modifier: Modifier = Modifier,
    procesando: Boolean = false
) {
    val pendiente = solicitud.estado == EstadoSolicitud.PENDIENTE
    var confirmarCancelar by remember(solicitud.idSolicitud) { mutableStateOf(false) }
    var confirmarAprobar by remember(solicitud.idSolicitud) { mutableStateOf(false) }
    var confirmarRechazar by remember(solicitud.idSolicitud) { mutableStateOf(false) }
    var textoAprobacion by remember(solicitud.idSolicitud) { mutableStateOf("") }
    var textoRechazo by remember(solicitud.idSolicitud) { mutableStateOf("") }

    if (confirmarCancelar) {
        AlertDialog(
            onDismissRequest = { confirmarCancelar = false },
            title = { Text("Cancelar solicitud") },
            text = { Text("¿Seguro que deseas cancelar esta solicitud?") },
            confirmButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        confirmarCancelar = false
                        onCancelar(solicitud.idSolicitud)
                    }
                ) { Text("Sí, cancelar", color = ErrorText) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarCancelar = false }) { Text("Volver") }
            }
        )
    }

    if (confirmarAprobar) {
        AlertDialog(
            onDismissRequest = { confirmarAprobar = false },
            title = { Text("Aprobar solicitud") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("¿Deseas aprobar esta solicitud y generar el préstamo pendiente?")
                    OutlinedTextField(
                        value = textoAprobacion,
                        onValueChange = { textoAprobacion = it },
                        label = { Text("Comentario (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        confirmarAprobar = false
                        onAprobar(
                            solicitud.idSolicitud,
                            textoAprobacion.trim().ifBlank { null }
                        )
                    }
                ) { Text("Aprobar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarAprobar = false }) { Text("Volver") }
            }
        )
    }

    if (confirmarRechazar) {
        AlertDialog(
            onDismissRequest = { confirmarRechazar = false },
            title = { Text("Rechazar solicitud") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Puedes indicar el motivo del rechazo.")
                    OutlinedTextField(
                        value = textoRechazo,
                        onValueChange = { textoRechazo = it },
                        label = { Text("Comentario (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        confirmarRechazar = false
                        onRechazar(
                            solicitud.idSolicitud,
                            textoRechazo.trim().ifBlank { null }
                        )
                    }
                ) { Text("Rechazar", color = ErrorText) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarRechazar = false }) { Text("Volver") }
            }
        )
    }

    val mostrarAcciones = pendiente ||
        solicitud.estado == EstadoSolicitud.APROBADA ||
        (solicitud.estado == EstadoSolicitud.RECHAZADA && !esEncargado)

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundClean,
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la solicitud") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundClean)
            )
        },
        bottomBar = {
            if (mostrarAcciones) {
                Surface(color = SurfaceWhite, shadowElevation = 4.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .navigationBarsPadding().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        when (solicitud.estado) {
                            EstadoSolicitud.PENDIENTE -> {
                                if (esEncargado) {
                                    OutlinedButton(
                                        onClick = { confirmarRechazar = true },
                                        enabled = !procesando,
                                        modifier = Modifier.weight(1f),
                                        border = BorderStroke(1.dp, ErrorBorder)
                                    ) { Text("Rechazar", color = ErrorText) }
                                    Button(
                                        onClick = { confirmarAprobar = true },
                                        enabled = !procesando,
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Aprobar") }
                                } else {
                                    OutlinedButton(
                                        onClick = { confirmarCancelar = true },
                                        enabled = !procesando,
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, ErrorBorder)
                                    ) { Text("Cancelar solicitud", color = ErrorText) }
                                }
                            }
                            EstadoSolicitud.APROBADA -> {
                                Button(
                                    onClick = { onVerPrestamo(solicitud.idSolicitud) },
                                    enabled = !procesando,
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Ver préstamo") }
                            }
                            EstadoSolicitud.RECHAZADA -> {
                                if (!esEncargado) {
                                    Button(
                                        onClick = onVolverASolicitar,
                                        enabled = !procesando,
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Volver a solicitar") }
                                }
                            }
                            EstadoSolicitud.CANCELADA -> Unit
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (esEncargado) {
                item {
                    TarjetaDetalleSolicitud {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp)
                                    .background(PrimaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
                            }
                            Column {
                                Text(solicitud.nombreEstudiante, color = TextDark,
                                    style = MaterialTheme.typography.titleMedium)
                                if (solicitud.codigoEstudiante.isNotBlank()) {
                                    Text("Cód. ${solicitud.codigoEstudiante}", color = TextMuted,
                                        style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            item {
                TarjetaDetalleSolicitud {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Solicitud N.º ${numeroVisual(numero)}", color = TextMuted,
                                style = MaterialTheme.typography.labelSmall)
                            Text("${solicitud.detalles.size} equipo(s)", color = TextDark,
                                style = MaterialTheme.typography.titleLarge)
                        }
                        BadgeEstado(
                            label = solicitud.estado.etiqueta,
                            tone = solicitud.estado.toTono()
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FechaDetalleSolicitud(
                            "Retiro previsto", solicitud.inicioReservaVisual,
                            Modifier.weight(1f)
                        )
                        FechaDetalleSolicitud(
                            "Devolución prevista", solicitud.finReservaVisual,
                            Modifier.weight(1f)
                        )
                    }
                    Text("Enviada: ${solicitud.fechaSolicitudVisual}", color = TextMuted,
                        style = MaterialTheme.typography.labelSmall)
                    if (solicitud.fechaRespuesta != null) {
                        Text("Respondida: ${solicitud.fechaRespuestaVisual}", color = TextMuted,
                            style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // El comentario ya pertenece a Solicitud, incluso cuando fue aprobada.
            if (!solicitud.comentarioRespuesta.isNullOrBlank()) {
                item {
                    val rechazo = solicitud.estado == EstadoSolicitud.RECHAZADA
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (rechazo) ErrorContainer else SurfaceWhite
                        ),
                        border = BorderStroke(1.dp, if (rechazo) ErrorBorder else Border)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                when (solicitud.estado) {
                                    EstadoSolicitud.APROBADA -> "Comentario de aprobación"
                                    EstadoSolicitud.RECHAZADA -> "Motivo del rechazo"
                                    EstadoSolicitud.CANCELADA -> "Comentario de cancelación"
                                    EstadoSolicitud.PENDIENTE -> "Comentario"
                                },
                                fontWeight = FontWeight.SemiBold,
                                color = if (rechazo) ErrorText else TextDark
                            )
                            Text(solicitud.comentarioRespuesta.orEmpty(), color = TextBody)
                        }
                    }
                }
            }

            item {
                TarjetaDetalleSolicitud {
                    Text("Equipos (${solicitud.detalles.size})",
                        style = MaterialTheme.typography.titleMedium, color = TextDark)
                }
            }
            items(solicitud.detalles, key = { it.idEquipo }) { equipo ->
                TarjetaDetalleSolicitud {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(42.dp)
                                .background(PrimarySoft, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Devices, contentDescription = null, tint = Primary)
                        }
                        Column {
                            Text(equipo.nombreEquipo, color = TextDark)
                            Text(equipo.codigoQrEquipo,
                                color = TextMuted, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            item {
                TarjetaDetalleSolicitud {
                    Text("Motivo", color = TextMuted,
                        style = MaterialTheme.typography.labelSmall)
                    Text(solicitud.motivoSolicitud, color = TextBody)
                }
            }

            item {
                TarjetaDetalleSolicitud {
                    Text("Seguimiento", color = TextDark,
                        style = MaterialTheme.typography.titleMedium)
                    EventoSolicitud(
                        titulo = "Solicitud enviada",
                        descripcion = solicitud.fechaSolicitudVisual,
                        completado = true
                    )
                    when (solicitud.estado) {
                        EstadoSolicitud.PENDIENTE -> EventoSolicitud(
                            "En revisión", "Esperando la respuesta del encargado", false
                        )
                        EstadoSolicitud.APROBADA -> EventoSolicitud(
                            "Solicitud aprobada",
                            if (solicitud.fechaRespuesta != null)
                                solicitud.fechaRespuestaVisual else "Aprobada por el encargado",
                            true
                        )
                        EstadoSolicitud.RECHAZADA -> EventoSolicitud(
                            "Solicitud rechazada",
                            if (solicitud.fechaRespuesta != null)
                                solicitud.fechaRespuestaVisual else "Rechazada por el encargado",
                            true, esError = true
                        )
                        EstadoSolicitud.CANCELADA -> EventoSolicitud(
                            "Solicitud cancelada",
                            if (solicitud.fechaRespuesta != null)
                                solicitud.fechaRespuestaVisual else "Cancelada por el estudiante",
                            true, esError = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaDetalleSolicitud(
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = contenido
        )
    }
}

@Composable
private fun FechaDetalleSolicitud(
    titulo: String,
    fecha: String,
    modifier: Modifier = Modifier
) {
    val partes = fecha.split(" ", limit = 2)
    Column(
        modifier = modifier.background(BackgroundClean, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(titulo, color = TextMuted, style = MaterialTheme.typography.labelSmall)
        Text(partes.firstOrNull().orEmpty().ifBlank { "Desconocido" }, color = TextDark)
        if (partes.size > 1) {
            Text(partes[1], color = TextBody, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun EventoSolicitud(
    titulo: String,
    descripcion: String,
    completado: Boolean,
    esError: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (completado) Icons.Default.CheckCircle
                else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = when {
                esError -> ErrorText
                completado -> Primary
                else -> TextHint
            },
            modifier = Modifier.size(20.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(titulo, color = TextDark, style = MaterialTheme.typography.bodyMedium)
            Text(descripcion, color = TextMuted,
                style = MaterialTheme.typography.labelSmall)
        }
    }
}
