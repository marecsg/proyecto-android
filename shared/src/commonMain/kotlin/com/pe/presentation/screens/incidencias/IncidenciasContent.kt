
package com.pe.presentation.screens.incidencias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Incidencia
import com.pe.domain.model.EstadoTicket
import com.pe.presentation.components.BadgeEstado
import com.pe.presentation.components.TonoEstado
import com.pe.presentation.theme.*

@Composable
fun IncidenciasContent(
    incidencias: List<Incidencia>,
    esEncargado: Boolean,
    onVerDetalle: (Int) -> Unit,
    onReportarIncidencia: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    error: String? = null,
    listadoEstudianteDisponible: Boolean = false
) {
    var busqueda by remember { mutableStateOf("") }

    val filtradas = incidencias.filter {
        busqueda.isBlank() ||
                it.descripcion.contains(busqueda, ignoreCase = true) ||
                it.tipoIncidencia.etiqueta.contains(
                    busqueda, ignoreCase = true
                ) ||
                it.estadoTicket.etiqueta.contains(
                    busqueda, ignoreCase = true
                ) ||
                it.nombreEquipo.orEmpty().contains(
                    busqueda, ignoreCase = true
                ) ||
                (esEncargado &&
                        it.nombreUsuarioReporta.orEmpty().contains(
                            busqueda, ignoreCase = true
                        ))
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        // Botón exclusivo del estudiante
        if (!esEncargado) {
            Button(
                onClick = onReportarIncidencia,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text("Reportar incidencia")
            }
        }

        // Buscador de incidencias
        if (esEncargado || listadoEstudianteDisponible) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text("Buscar incidencias...")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {

                // Endpoint de estudiante pendiente
                !esEncargado &&
                        !listadoEstudianteDisponible -> {
                    MensajeIncidencias(
                        titulo = "Mis incidencias",
                        descripcion =
                            "La consulta de incidencias por " +
                                    "estudiante está pendiente en el backend.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary
                    )
                }

                error != null -> {
                    MensajeIncidencias(
                        titulo = "Error al cargar incidencias",
                        descripcion = error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                filtradas.isEmpty() -> {
                    MensajeIncidencias(
                        titulo = "No hay incidencias",
                        descripcion = if (busqueda.isNotBlank()) {
                            "No se encontraron coincidencias."
                        } else {
                            "No hay incidencias para mostrar."
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = filtradas,
                            key = { it.idTicket }
                        ) { incidencia ->

                            TarjetaIncidencia(
                                incidencia = incidencia,
                                esEncargado = esEncargado,
                                onClick = {
                                    onVerDetalle(incidencia.idTicket)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaIncidencia(
    incidencia: Incidencia,
    esEncargado: Boolean,
    onClick: () -> Unit
) {
    val tono = when (incidencia.estadoTicket) {
        EstadoTicket.PENDIENTE -> TonoEstado.WARNING
        EstadoTicket.EN_PROCESO -> TonoEstado.INFO
        EstadoTicket.RESUELTO -> TonoEstado.SUCCESS
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Incidencia #${incidencia.idTicket}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                BadgeEstado(
                    label = incidencia.estadoTicket.etiqueta,
                    tone = tono
                )
            }

            Text(
                text = incidencia.tipoIncidencia.etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = Primary
            )

            Text(
                text = incidencia.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Equipo: " +
                        (incidencia.nombreEquipo
                            ?: "Sin equipo asociado"),
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            if (esEncargado) {
                Text(
                    text = "Reportado por: " +
                            (incidencia.nombreUsuarioReporta
                                ?: "No disponible"),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Text(
                text = "Fecha: " +
                        (incidencia.fechaReporte
                            ?.substringBefore("T")
                            ?: "No disponible"),
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun MensajeIncidencias(
    titulo: String,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Report,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            color = TextDark
        )

        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
    }
}
