
package com.pe.presentation.screens.equipos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Equipo
import com.pe.domain.model.EstadoFisicoEquipo
import com.pe.presentation.components.BadgeEstado
import com.pe.presentation.components.toTono
import com.pe.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEquipoContent(
    equipo: Equipo,
    onBack: () -> Unit,
    onRequestLoan: () -> Unit,
    onReportIncident: () -> Unit,
    esEncargado: Boolean,
) {
    Scaffold(
        containerColor = BackgroundClean,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del equipo",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (!esEncargado) {
                Button(
                    onClick = onRequestLoan,
                    enabled = equipo.estaDisponible,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Solicitar préstamo")
                 }
                }

                OutlinedButton(
                    onClick = onReportIncident,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        WarningBorder
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceWhite,
                        contentColor = WarningText
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Reportar incidencia")
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            // TARJETA PRINCIPAL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                border = BorderStroke(1.dp, Border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                PrimarySoft,
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "EQUIPO DE LABORATORIO",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )

                        Text(
                            text = equipo.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextDark
                        )

                        if (esEncargado) {
                            Text(
                                text = "Código: ${equipo.codigoQr}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        BadgeEstado(
                            label = equipo.disponibilidad.etiqueta,
                            tone = equipo.disponibilidad.toTono()
                        )
                    }
                }
            }


            // DESCRIPCIÓN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    Text(
                        text = equipo.descripcion.ifBlank {
                            "Sin descripción disponible"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextBody
                    )
                }
            }

            // INFORMACIÓN DEL EQUIPO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    Text(
                        text = "Información del equipo",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark
                    )

                    HorizontalDivider(color = Divider)

                    DatoEquipo(
                        titulo = "Ubicación",
                        valor = equipo.nombreAmbiente,
                        icono = {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                tint = Primary
                            )
                        }
                    )

                    HorizontalDivider(color = Divider)

                    DatoEquipo(
                        titulo = "Estado físico",
                        valor = equipo.estadoFisico.etiqueta,
                        icono = {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = when (equipo.estadoFisico) {
                                    EstadoFisicoEquipo.OPERATIVO ->
                                        SuccessText

                                    EstadoFisicoEquipo.MANTENIMIENTO ->
                                        WarningText

                                    EstadoFisicoEquipo.DEFECTUOSO ->
                                        ErrorText
                                }
                            )
                        }
                    )

                    HorizontalDivider(color = Divider)
                }
            }

            if (!equipo.estaDisponible) {
                Text(
                    text = "Este equipo no está disponible para préstamo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun DatoEquipo(
    titulo: String,
    valor: String,
    icono: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    PrimarySoft,
                    RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            icono()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
            )
        }
    }
}
