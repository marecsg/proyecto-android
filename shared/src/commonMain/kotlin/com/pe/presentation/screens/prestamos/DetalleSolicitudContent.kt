
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
import com.pe.domain.model.Solicitud
import com.pe.domain.model.EstadoSolicitud
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
    onAprobar: (Int) -> Unit,
    onRechazar: (Int, String?) -> Unit,
    onVerPrestamo: (Int) -> Unit,
    onVolverASolicitar: () -> Unit,
    modifier: Modifier = Modifier,
    procesando: Boolean = false
) {

    val pendiente = solicitud.estado == EstadoSolicitud.PENDIENTE

    var mostrarConfirmacionCancelar by remember {
        mutableStateOf(false)
    }

    var mostrarConfirmacionAprobar by remember {
        mutableStateOf(false)
    }

    var mostrarDialogoRechazo by remember {
        mutableStateOf(false)
    }

    var comentarioRechazo by remember {
        mutableStateOf("")
    }

    // CONFIRMACIÓN PARA CANCELAR
    if (mostrarConfirmacionCancelar) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacionCancelar = false
            },
            title = {
                Text("Cancelar solicitud")
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas cancelar " +
                            "esta solicitud?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionCancelar = false
                        onCancelar(solicitud.idSolicitud)
                    },
                    enabled = !procesando
                ) {
                    Text(
                        text = "Sí, cancelar",
                        color = ErrorText
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionCancelar = false
                    }
                ) {
                    Text("Volver")
                }
            }
        )
    }

    // CONFIRMACIÓN PARA APROBAR
    if (mostrarConfirmacionAprobar) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacionAprobar = false
            },
            title = {
                Text("Aprobar solicitud")
            },
            text = {
                Text(
                    "¿Deseas aprobar esta solicitud y " +
                            "generar el préstamo pendiente?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionAprobar = false
                        onAprobar(solicitud.idSolicitud)
                    },
                    enabled = !procesando
                ) {
                    Text("Aprobar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionAprobar = false
                    }
                ) {
                    Text("Volver")
                }
            }
        )
    }

    // RECHAZO CON COMENTARIO OPCIONAL
    if (mostrarDialogoRechazo) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoRechazo = false
            },
            title = {
                Text("Rechazar solicitud")
            },
            text = {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Puedes escribir un motivo de rechazo."
                    )

                    OutlinedTextField(
                        value = comentarioRechazo,
                        onValueChange = {
                            comentarioRechazo = it
                        },
                        label = {
                            Text("Comentario (opcional)")
                        },
                        placeholder = {
                            Text(
                                "Ej. Equipo en mantenimiento"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoRechazo = false

                        onRechazar(
                            solicitud.idSolicitud,
                            comentarioRechazo
                                .trim()
                                .ifBlank { null }
                        )
                    },
                    enabled = !procesando
                ) {
                    Text(
                        text = "Rechazar",
                        color = ErrorText
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoRechazo = false
                    }
                ) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundClean,

        // CABECERA
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de la solicitud",
                        style =
                            MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundClean
                    )
            )
        },

        // BOTONES SEGÚN ROL Y ESTADO
        bottomBar = {

            val mostrarAcciones =
                pendiente ||
                        solicitud.estado == EstadoSolicitud.APROBADA ||
                        (
                                solicitud.estado ==
                                        EstadoSolicitud.RECHAZADA &&
                                        !esEncargado
                                )

            if (mostrarAcciones) {
                Surface(
                    color = SurfaceWhite,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        when (solicitud.estado) {

                            EstadoSolicitud.PENDIENTE -> {

                                if (esEncargado) {

                                    OutlinedButton(
                                        onClick = {
                                            comentarioRechazo = ""
                                            mostrarDialogoRechazo =
                                                true
                                        },
                                        enabled = !procesando,
                                        modifier =
                                            Modifier.weight(1f),
                                        shape =
                                            RoundedCornerShape(12.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            ErrorBorder
                                        ),
                                        colors =
                                            ButtonDefaults
                                                .outlinedButtonColors(
                                                    contentColor =
                                                        ErrorText
                                                )
                                    ) {
                                        Text("Rechazar")
                                    }

                                    Button(
                                        onClick = {
                                            mostrarConfirmacionAprobar =
                                                true
                                        },
                                        enabled = !procesando,
                                        modifier =
                                            Modifier.weight(1f),
                                        shape =
                                            RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Aprobar")
                                    }

                                } else {

                                    OutlinedButton(
                                        onClick = {
                                            mostrarConfirmacionCancelar =
                                                true
                                        },
                                        enabled = !procesando,
                                        modifier =
                                            Modifier.fillMaxWidth(),
                                        shape =
                                            RoundedCornerShape(12.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            ErrorBorder
                                        ),
                                        colors =
                                            ButtonDefaults
                                                .outlinedButtonColors(
                                                    contentColor =
                                                        ErrorText
                                                )
                                    ) {
                                        Text("Cancelar solicitud")
                                    }
                                }
                            }

                            EstadoSolicitud.APROBADA -> {
                                Button(
                                    onClick = {
                                        onVerPrestamo(
                                            solicitud.idSolicitud
                                        )
                                    },
                                    enabled = !procesando,
                                    modifier =
                                        Modifier.fillMaxWidth(),
                                    shape =
                                        RoundedCornerShape(12.dp)
                                ) {
                                    Text("Ver préstamo")
                                }
                            }

                            EstadoSolicitud.RECHAZADA -> {
                                if (!esEncargado) {
                                    Button(
                                        onClick =
                                            onVolverASolicitar,
                                        enabled = !procesando,
                                        modifier =
                                            Modifier.fillMaxWidth(),
                                        shape =
                                            RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Volver a solicitar")
                                    }
                                }
                            }

                            EstadoSolicitud.CANCELADA -> Unit
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // INFORMACIÓN DEL ESTUDIANTE SOLO ENCARGADO
            if (esEncargado) {
                item {
                    TarjetaDetalleSolicitud {
                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        PrimaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Primary
                                )
                            }

                            Column {
                                Text(
                                    text =
                                        solicitud.nombreEstudiante,
                                    style =
                                        MaterialTheme.typography
                                            .titleMedium,
                                    color = TextDark
                                )

                                Text(
                                    text = "Cód. " +
                                            solicitud.codigoEstudiante
                                                .ifBlank {
                                                    "Desconocido"
                                                },
                                    style =
                                        MaterialTheme.typography
                                            .labelSmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // RESUMEN DE LA SOLICITUD
            item {
                TarjetaDetalleSolicitud {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column {
                                Text(
                                    text = "Solicitud N.º ${
                                        numeroVisual(numero)
                                    }",
                                    style =
                                        MaterialTheme.typography
                                            .labelSmall,
                                    color = TextMuted
                                )

                                Text(
                                    text =
                                        "${solicitud.detalles.size} " +
                                                if (
                                                    solicitud.detalles.size == 1
                                                ) "equipo" else "equipos",
                                    style =
                                        MaterialTheme.typography
                                            .titleLarge,
                                    color = TextDark
                                )
                            }

                            BadgeEstado(
                                label =
                                    solicitud.estado.etiqueta,
                                tone =
                                    solicitud.estado.toTono()
                            )
                        }

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            FechaDetalleSolicitud(
                                titulo = "Retiro previsto",
                                fecha =
                                    solicitud.inicioReservaVisual,
                                modifier =
                                    Modifier.weight(1f)
                            )

                            FechaDetalleSolicitud(
                                titulo = "Devolución prevista",
                                fecha =
                                    solicitud.finReservaVisual,
                                modifier =
                                    Modifier.weight(1f)
                            )
                        }

                        Text(
                            text = "Enviada: ${
                                solicitud.fechaSolicitudVisual
                            }",
                            style =
                                MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            // COMENTARIO DE RECHAZO
            // NUNCA APARECE EN CANCELADA
            if (
                solicitud.estado ==
                EstadoSolicitud.RECHAZADA &&
                !solicitud.comentarioRechazo.isNullOrBlank()
            ) {
                item {
                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(16.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    ErrorContainer
                            ),
                        border = BorderStroke(
                            1.dp,
                            ErrorBorder
                        )
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(6.dp)
                        ) {

                            Text(
                                text =
                                    "Respuesta del encargado",
                                style =
                                    MaterialTheme.typography
                                        .labelMedium,
                                color = ErrorText,
                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Text(
                                text =
                                    solicitud.comentarioRechazo
                                        .orEmpty(),
                                style =
                                    MaterialTheme.typography
                                        .bodyMedium,
                                color = TextBody
                            )
                        }
                    }
                }
            }

            // EQUIPOS
            item {
                TarjetaDetalleSolicitud {
                    Text(
                        text = "Equipos (" +
                                solicitud.detalles.size + ")",
                        style =
                            MaterialTheme.typography.titleMedium,
                        color = TextDark
                    )
                }
            }

            // MISMO DISEÑO PARA UNO O VARIOS
            items(
                items = solicitud.detalles,
                key = { it.idEquipo }
            ) { equipo ->

                TarjetaDetalleSolicitud {
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    PrimarySoft,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Devices,
                                contentDescription = null,
                                tint = Primary
                            )
                        }

                        Column(
                            modifier =
                                Modifier.weight(1f),
                            verticalArrangement =
                                Arrangement.spacedBy(4.dp)
                        ) {

                            Text(
                                text = equipo.nombreEquipo,
                                style =
                                    MaterialTheme.typography
                                        .bodyMedium,
                                color = TextDark
                            )

                            Text(
                                text = equipo.codigoQrEquipo,
                                style =
                                    MaterialTheme.typography
                                        .labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            // MOTIVO
            item {
                TarjetaDetalleSolicitud {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            text = "Motivo",
                            style =
                                MaterialTheme.typography
                                    .labelSmall,
                            color = TextMuted
                        )

                        Text(
                            text = solicitud.motivoSolicitud,
                            style =
                                MaterialTheme.typography
                                    .bodyMedium,
                            color = TextBody
                        )
                    }
                }
            }

            // SEGUIMIENTO
            item {
                TarjetaDetalleSolicitud {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(14.dp)
                    ) {

                        Text(
                            text = "Seguimiento",
                            style =
                                MaterialTheme.typography
                                    .titleMedium,
                            color = TextDark
                        )

                        EventoSolicitud(
                            titulo = "Solicitud enviada",
                            descripcion =
                                solicitud.fechaSolicitudVisual,
                            completado = true
                        )

                        when (solicitud.estado) {

                            EstadoSolicitud.PENDIENTE -> {
                                EventoSolicitud(
                                    titulo = "En revisión",
                                    descripcion =
                                        "Esperando la respuesta " +
                                                "del encargado",
                                    completado = false
                                )
                            }

                            EstadoSolicitud.APROBADA -> {
                                EventoSolicitud(
                                    titulo =
                                        "Solicitud aprobada",
                                    descripcion =
                                        "Aprobada por el encargado",
                                    completado = true
                                )
                            }

                            EstadoSolicitud.RECHAZADA -> {
                                EventoSolicitud(
                                    titulo =
                                        "Solicitud rechazada",
                                    descripcion =
                                        "Rechazada por el encargado",
                                    completado = true,
                                    esError = true
                                )
                            }

                            EstadoSolicitud.CANCELADA -> {
                                EventoSolicitud(
                                    titulo =
                                        "Solicitud cancelada",
                                    descripcion =
                                        "Cancelada por el estudiante",
                                    completado = true,
                                    esError = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// TARJETA BASE
@Composable
private fun TarjetaDetalleSolicitud(
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
                .padding(16.dp),
            content = contenido
        )
    }
}

// FECHA DEL RESUMEN
@Composable
private fun FechaDetalleSolicitud(
    titulo: String,
    fecha: String,
    modifier: Modifier = Modifier
) {

    val partes = fecha.split(" ", limit = 2)

    Column(
        modifier = modifier
            .background(
                BackgroundClean,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalArrangement =
            Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = titulo,
            style =
                MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        Text(
            text = partes.firstOrNull()
                ?.takeIf { it.isNotBlank() }
                ?: "Desconocido",
            style =
                MaterialTheme.typography.bodyMedium,
            color = TextDark
        )

        if (partes.size > 1) {
            Text(
                text = partes[1],
                style =
                    MaterialTheme.typography.labelSmall,
                color = TextBody
            )
        }
    }
}

// EVENTOS DEL SEGUIMIENTO
@Composable
private fun EventoSolicitud(
    titulo: String,
    descripcion: String,
    completado: Boolean,
    esError: Boolean = false
) {

    Row(
        horizontalArrangement =
            Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {

        Icon(
            imageVector = if (completado) {
                Icons.Default.CheckCircle
            } else {
                Icons.Default.RadioButtonUnchecked
            },
            contentDescription = null,
            tint = when {
                esError -> ErrorText
                completado -> Primary
                else -> TextHint
            },
            modifier = Modifier.size(20.dp)
        )

        Column(
            verticalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = titulo,
                style =
                    MaterialTheme.typography.bodyMedium,
                color = TextDark
            )

            Text(
                text = descripcion,
                style =
                    MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}
