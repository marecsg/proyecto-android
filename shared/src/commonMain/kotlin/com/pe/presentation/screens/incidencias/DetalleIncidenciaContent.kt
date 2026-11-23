
package com.pe.presentation.screens.incidencias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Incidencia
import com.pe.domain.model.EstadoTicket
import com.pe.presentation.components.BadgeEstado
import com.pe.presentation.components.TonoEstado
import com.pe.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleIncidenciaContent(
    incidencia: Incidencia,
    esEncargado: Boolean,
    onVolver: () -> Unit,
    onActualizarEstado: (Int, EstadoTicket, String?) -> Unit,
    procesando: Boolean = false,
    error: String? = null
) {

    var respuesta by remember(incidencia.idTicket) {
        mutableStateOf(incidencia.respuestaEncargado.orEmpty())
    }

    var confirmarResolucion by remember(incidencia.idTicket) {
        mutableStateOf(false)
    }

    val estado = incidencia.estadoTicket

    val tono = when (estado) {
        EstadoTicket.PENDIENTE -> TonoEstado.WARNING
        EstadoTicket.EN_PROCESO -> TonoEstado.INFO
        EstadoTicket.RESUELTO -> TonoEstado.SUCCESS
    }

    val puedeIniciar = esEncargado &&
            estado == EstadoTicket.PENDIENTE

    val puedeResolver = esEncargado &&
            estado == EstadoTicket.EN_PROCESO

    // Confirmación antes de resolver
    if (confirmarResolucion) {
        AlertDialog(
            onDismissRequest = {
                confirmarResolucion = false
            },
            title = {
                Text("Resolver incidencia")
            },
            text = {
                Text(
                    "¿Confirmas que la incidencia fue " +
                            "atendida y deseas marcarla como resuelta?"
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        confirmarResolucion = false

                        onActualizarEstado(
                            incidencia.idTicket,
                            EstadoTicket.RESUELTO,
                            respuesta.trim()
                        )
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmarResolucion = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundClean,

        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle de incidencia")
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
            if (puedeIniciar || puedeResolver) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite)
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !procesando &&
                                (
                                        puedeIniciar ||
                                                (puedeResolver &&
                                                        respuesta.isNotBlank())
                                        ),
                        onClick = {
                            when {
                                puedeIniciar -> {
                                    onActualizarEstado(
                                        incidencia.idTicket,
                                        EstadoTicket.EN_PROCESO,
                                        null
                                    )
                                }

                                puedeResolver -> {
                                    confirmarResolucion = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (procesando) {
                            Text("Procesando...")
                        } else {
                            Text(
                                if (puedeIniciar) {
                                    "Poner en proceso"
                                } else {
                                    "Resolver incidencia"
                                }
                            )
                        }
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // IDENTIFICACIÓN Y ESTADO
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(16.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = incidencia.codigoCorrelativo?.takeIf { it.isNotBlank() } ?: "----",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextDark
                    )

                    BadgeEstado(
                        label = estado.etiqueta,
                        tone = tono
                    )

                    HorizontalDivider()

                    DatoIncidencia(
                        "Tipo de incidencia",
                        incidencia.tipoIncidencia.etiqueta
                    )

                    DatoIncidencia(
                        "Laboratorio",
                        incidencia.nombreAmbiente
                            ?: "No disponible"
                    )

                    DatoIncidencia(
                        "Equipo",
                        incidencia.nombreEquipo
                            ?: "Sin equipo asociado"
                    )

                    DatoIncidencia(
                        "Reportado por",
                        incidencia.nombreUsuarioReporta
                            ?: "No disponible"
                    )

                    DatoIncidencia(
                        "Fecha del reporte",
                        incidencia.fechaReporteVisual
                    )
                }
            }

            // DESCRIPCIÓN
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(16.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Descripción del problema",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark
                    )

                    Text(
                        incidencia.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextBody
                    )
                }
            }

            // RESPUESTA PARA RESOLVER
            if (puedeResolver) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceWhite
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            "Respuesta del encargado",
                            style =
                                MaterialTheme.typography.titleMedium,
                            color = TextDark
                        )

                        Text(
                            "Describe cómo se atendió " +
                                    "y solucionó la incidencia.",
                            style =
                                MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )

                        OutlinedTextField(
                            value = respuesta,
                            onValueChange = {
                                respuesta = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Escribe la solución...")
                            },
                            minLines = 4,
                            maxLines = 6,
                            enabled = !procesando,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // MOSTRAR RESPUESTA EXISTENTE
            if (estado == EstadoTicket.RESUELTO) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            "Incidencia resuelta",
                            style =
                                MaterialTheme.typography.titleMedium,
                            color = SuccessText
                        )

                        Text(
                            incidencia.respuestaEncargado
                                ?: "Sin respuesta registrada",
                            color = TextBody
                        )

                        DatoIncidencia(
                            "Fecha de resolución",
                            incidencia.fechaResolucionVisual
                        )
                    }
                }
            }

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Componente reutilizable para mostrar información
@Composable
private fun DatoIncidencia(
    titulo: String,
    valor: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDark
        )
    }
}
