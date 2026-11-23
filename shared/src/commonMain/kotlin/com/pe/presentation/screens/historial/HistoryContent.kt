
package com.pe.presentation.screens.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Historial
import com.pe.presentation.components.Buscador
import com.pe.presentation.components.HistoryCard
import com.pe.presentation.theme.*
import com.pe.presentation.viewmodel.HistorialUiState

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun HistoryContent(
    estado: HistorialUiState,
    esEncargado: Boolean,
    onRecargar: () -> Unit,
    modifier: Modifier = Modifier,
    onVerDetalle: ((Historial) -> Unit)? = null
) {
    var filtroSeleccionado by remember {
        mutableStateOf<String?>(null)
    }

    var busqueda by remember {
        mutableStateOf("")
    }

    var mostrarFiltros by remember {
        mutableStateOf(false)
    }

    var borradorFiltro by remember {
        mutableStateOf<String?>(null)
    }

    val filtros: List<Pair<String, String?>> = listOf(
        "Todas" to null,
        "Solicitudes" to "SOLICITUD",
        "Préstamos" to "PRESTAMO",
        "Incidencias" to "INCIDENCIA"
    )


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
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Filtros",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextDark
                    )

                    TextButton(
                        onClick = {
                            borradorFiltro = null
                        }
                    ) {
                        Text("Limpiar")
                    }
                }

                Text(
                    text = "Tipo de actividad",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filtros.forEach { opcion ->
                        FilterChip(
                            selected = borradorFiltro == opcion.second,
                            onClick = {
                                borradorFiltro = opcion.second
                            },
                            label = {
                                Text(opcion.first)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        filtroSeleccionado = borradorFiltro
                        mostrarFiltros = false
                    },
                    modifier = Modifier.fillMaxWidth()
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

        when (estado) {

            HistorialUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Preparando historial...",
                        color = TextMuted
                    )
                }
            }

            HistorialUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }

            HistorialUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "Sin actividades todavía",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextDark
                        )

                        Text(
                            text = "Aquí aparecerán los movimientos " +
                                    "registrados en LabManager.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is HistorialUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "No se pudo cargar el historial",
                        style = MaterialTheme.typography.titleMedium,
                        color = ErrorText
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = estado.mensaje,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(onClick = onRecargar) {
                        Text("Reintentar")
                    }
                }
            }

            is HistorialUiState.Success -> {


                val registrosFiltrados = estado.historial
                    .filter { registro ->

                        val coincideTipo =
                            filtroSeleccionado == null ||
                                    registro.tipoEntidad.equals(
                                        filtroSeleccionado,
                                        ignoreCase = true
                                    )

                        val consulta = busqueda.trim()

                        val coincideBusqueda =
                            consulta.isBlank() ||
                                    registro.tipoAccion
                                        .replace("_", " ")
                                        .contains(consulta, ignoreCase = true) ||
                                    registro.descripcion.orEmpty()
                                        .contains(consulta, ignoreCase = true) ||
                                    registro.tipoEntidad
                                        .contains(consulta, ignoreCase = true) ||
                                    registro.fechaAccionVisual
                                        .contains(consulta, ignoreCase = true)

                        coincideTipo && coincideBusqueda
                    }
                    .sortedByDescending { it.fechaAccion }


                // BUSCADOR + FILTROS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 8.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Buscador(
                        texto = busqueda,
                        onValueChange = {
                            busqueda = it
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = "Buscar actividades..."
                    )

                    BadgedBox(
                        badge = {
                            if (filtroSeleccionado != null) {
                                Badge {
                                    Text("1")
                                }
                            }
                        }
                    ) {
                        OutlinedIconButton(
                            onClick = {
                                borradorFiltro = filtroSeleccionado
                                mostrarFiltros = true
                            },
                            modifier = Modifier.size(56.dp),
                            shape = androidx.compose.foundation.shape
                                .RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Abrir filtros",
                                tint = if (filtroSeleccionado != null)
                                    Primary else TextDark
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 4.dp,
                            bottom = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    TextButton(
                        onClick = onRecargar
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            "Actualizar",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }


                if (registrosFiltrados.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay actividades de esta categoría.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = registrosFiltrados,
                            key = { it.idHistorial }
                        ) { registro ->

                            HistoryCard(
                                historial = registro,
                                onVerDetalle = onVerDetalle
                            )
                        }
                    }
                }
            }
        }
    }
}
