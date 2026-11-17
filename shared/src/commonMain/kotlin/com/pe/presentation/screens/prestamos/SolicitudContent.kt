
package com.pe.presentation.screens.solicitudes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Solicitud
import com.pe.presentation.components.BotonPrincipal
import com.pe.presentation.components.SolicitudCard
import com.pe.presentation.theme.*

@Composable
fun SolicitudesContent(
    solicitudes: List<Solicitud>,
    busqueda: String,
    esEncargado: Boolean,
    onVerDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    codigosPorEstudiante: Map<Int, String> = emptyMap(),
    cargando: Boolean = false,
    onNuevaSolicitud: () -> Unit = {},
    error: String? = null
) {

    // FILTRO
    val solicitudesFiltradas = solicitudes.filter { solicitud ->

        val coincideEquipo = solicitud.detalles.any { equipo ->
            equipo.nombreEquipo.contains(
                busqueda,
                ignoreCase = true
            ) || equipo.codigoQrEquipo.contains(
                busqueda,
                ignoreCase = true
            )
        }

        val coincideAmbiente = solicitud.nombreAmbiente.contains(
            busqueda,
            ignoreCase = true
        )

        val coincideMotivo = solicitud.motivoSolicitud.contains(
            busqueda,
            ignoreCase = true
        )

        val coincideEstudiante = esEncargado && (
                solicitud.nombreEstudiante.contains(
                    busqueda,
                    ignoreCase = true
                ) ||
                        codigosPorEstudiante[
                            solicitud.idEstudiante
                        ].orEmpty().contains(
                            busqueda,
                            ignoreCase = true
                        )
                )

        busqueda.isBlank() ||
                coincideEquipo ||
                coincideAmbiente ||
                coincideMotivo ||
                coincideEstudiante
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        when {

            // CARGANDO SOLICITUDES
            cargando -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    ),
                    color = Primary
                )
            }

            // ERROR AL CONSULTAR EL BACKEND
            error != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = "No se pudieron cargar las solicitudes",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // SIN SOLICITUDES O SIN RESULTADOS
            solicitudesFiltradas.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = if (busqueda.isBlank()) {
                            "No hay solicitudes"
                        } else {
                            "Sin resultados"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (busqueda.isBlank()) {
                            if (esEncargado) {
                                "Todavía no hay solicitudes " +
                                        "para este ambiente."
                            } else {
                                "Todavía no has realizado " +
                                        "ninguna solicitud."
                            }
                        } else {
                            "No encontramos solicitudes que " +
                                    "coincidan con tu búsqueda."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // LISTA DE SOLICITUDES
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    itemsIndexed(
                        items = solicitudesFiltradas,
                        key = { _, solicitud ->
                            solicitud.idSolicitud
                        }
                    ) { indice, solicitud ->

                        // EL NÚMERO VISUAL SE MANTIENE
                        // AUNQUE SE FILTRE LA LISTA
                        val numeroOriginal = solicitudes.indexOfFirst {
                            it.idSolicitud == solicitud.idSolicitud
                        } + 1

                        SolicitudCard(
                            solicitud = solicitud,
                            numero = numeroOriginal,
                            mostrarEstudiante = esEncargado,
                            codigoEstudiante = if (esEncargado) {
                                codigosPorEstudiante[
                                    solicitud.idEstudiante
                                ]
                            } else {
                                null
                            },
                            onClick = onVerDetalle
                        )
                    }
                }
            }
        }
    }
}
