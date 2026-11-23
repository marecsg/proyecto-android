
package com.pe.presentation.screens.incidencias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Incidencia
import com.pe.domain.model.EstadoTicket
import com.pe.domain.model.TipoIncidencia
import com.pe.presentation.components.BadgeEstado
import com.pe.presentation.components.Buscador
import com.pe.presentation.components.TonoEstado
import com.pe.presentation.theme.*

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
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
    var busqueda by remember {
        mutableStateOf("")
    }

    // FILTROS APLICADOS
    var estadoSeleccionado by remember {
        mutableStateOf<EstadoTicket?>(null)
    }

    var tipoSeleccionado by remember {
        mutableStateOf<TipoIncidencia?>(null)
    }

    // PANEL DE FILTROS
    var mostrarFiltros by remember {
        mutableStateOf(false)
    }

    // SELECCIONES TEMPORALES
    var borradorEstado by remember {
        mutableStateOf<EstadoTicket?>(null)
    }

    var borradorTipo by remember {
        mutableStateOf<TipoIncidencia?>(null)
    }

    val filtrosActivos =
        (if (estadoSeleccionado != null) 1 else 0) +
                (if (tipoSeleccionado != null) 1 else 0)

    // FILTRADO LOCAL
    val filtradas = incidencias.filter { incidencia ->

        val coincideBusqueda =
            busqueda.isBlank() ||
                    incidencia.descripcion.contains(
                        busqueda, ignoreCase = true
                    ) ||
                    incidencia.tipoIncidencia.etiqueta.contains(
                        busqueda, ignoreCase = true
                    ) ||
                    incidencia.estadoTicket.etiqueta.contains(
                        busqueda, ignoreCase = true
                    ) ||
                    incidencia.nombreEquipo.orEmpty().contains(
                        busqueda, ignoreCase = true
                    ) ||
                    incidencia.nombreAmbiente.orEmpty().contains(
                        busqueda, ignoreCase = true
                    ) ||
                    (esEncargado &&
                            incidencia.nombreUsuarioReporta.orEmpty()
                                .contains(busqueda, ignoreCase = true))

        val coincideEstado =
            estadoSeleccionado == null ||
                    incidencia.estadoTicket == estadoSeleccionado

        val coincideTipo =
            tipoSeleccionado == null ||
                    incidencia.tipoIncidencia == tipoSeleccionado

        coincideBusqueda && coincideEstado && coincideTipo
    }

    // PANEL INFERIOR
    if (mostrarFiltros) {
        ModalBottomSheet(
            onDismissRequest = {
                mostrarFiltros = false
            },
            containerColor = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text(
                        "Filtros",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextDark
                    )

                    TextButton(
                        onClick = {
                            borradorEstado = null
                            borradorTipo = null
                        }
                    ) {
                        Text("Limpiar")
                    }
                }

                Text(
                    "Estado",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = borradorEstado == null,
                        onClick = { borradorEstado = null },
                        label = { Text("Todas") }
                    )

                    EstadoTicket.entries.forEach { estado ->
                        FilterChip(
                            selected = borradorEstado == estado,
                            onClick = {
                                borradorEstado = estado
                            },
                            label = {
                                Text(estado.etiqueta)
                            }
                        )
                    }
                }

                Text(
                    "Tipo de incidencia",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = borradorTipo == null,
                        onClick = { borradorTipo = null },
                        label = { Text("Todos") }
                    )

                    TipoIncidencia.entries.forEach { tipo ->
                        FilterChip(
                            selected = borradorTipo == tipo,
                            onClick = {
                                borradorTipo = tipo
                            },
                            label = {
                                Text(tipo.etiqueta)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        estadoSeleccionado = borradorEstado
                        tipoSeleccionado = borradorTipo
                        mostrarFiltros = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aplicar filtros")
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundClean)
    ) {

        // BUSCADOR Y FILTROS
        if (esEncargado || listadoEstudianteDisponible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                Buscador(
                    texto = busqueda,
                    onValueChange = {
                        busqueda = it
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = "Buscar incidencias..."
                )

                BadgedBox(
                    badge = {
                        if (filtrosActivos > 0) {
                            Badge {
                                Text(filtrosActivos.toString())
                            }
                        }
                    }
                ) {
                    OutlinedIconButton(
                        onClick = {
                            borradorEstado = estadoSeleccionado
                            borradorTipo = tipoSeleccionado
                            mostrarFiltros = true
                        },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Abrir filtros",
                            tint = if (filtrosActivos > 0)
                                Primary else TextDark
                        )
                    }
                }
            }

            // FILTROS RÁPIDOS DE ESTADO
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = estadoSeleccionado == null,
                    onClick = {
                        estadoSeleccionado = null
                    },
                    label = { Text("Todas") }
                )

                EstadoTicket.entries.forEach { estado ->
                    FilterChip(
                        selected = estadoSeleccionado == estado,
                        onClick = {
                            estadoSeleccionado = estado
                        },
                        label = {
                            Text(estado.etiqueta)
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                !esEncargado &&
                        !listadoEstudianteDisponible -> {
                    MensajeIncidencias(
                        titulo = "Mis incidencias",
                        descripcion =
                            "La consulta de incidencias del estudiante " +
                                    "no está disponible.",
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
                        descripcion =
                            if (busqueda.isNotBlank() ||
                                filtrosActivos > 0) {
                                "No se encontraron coincidencias."
                            } else {
                                "No hay incidencias para mostrar."
                            },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 96.dp
                        ),
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

            if (!esEncargado) {
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    onClick = onReportarIncidencia
                ) {
                    Text("+ Reportar incidencia")
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
                    text = incidencia.codigoCorrelativo?.takeIf { it.isNotBlank() } ?: "----",
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
                text = "Fecha: ${incidencia.fechaReporteVisual}",
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
