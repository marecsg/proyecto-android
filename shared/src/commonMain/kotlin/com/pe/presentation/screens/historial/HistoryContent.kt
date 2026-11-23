
package com.pe.presentation.screens.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Historial
import com.pe.presentation.components.HistoryCard
import com.pe.presentation.theme.*
import com.pe.presentation.viewmodel.HistorialUiState

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

    val filtros = listOf(
        "Todas" to null,
        "Solicitudes" to "SOLICITUD",
        "Préstamos" to "PRESTAMO",
        "Incidencias" to "INCIDENCIA"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundClean)
    ) {

        // ENCABEZADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 8.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Actividad reciente",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                Text(
                    text = if (esEncargado) {
                        "Acciones registradas por ti"
                    } else {
                        "Movimientos relacionados contigo"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

            IconButton(
                onClick = onRecargar,
                enabled = estado !is HistorialUiState.Loading
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar historial",
                    tint = Primary
                )
            }
        }

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
                        filtroSeleccionado == null ||
                                registro.tipoEntidad.equals(
                                    filtroSeleccionado,
                                    ignoreCase = true
                                )
                    }
                    .sortedByDescending { it.fechaAccion }

                // FILTROS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filtros.forEach { (titulo, valor) ->
                        FilterChip(
                            selected = filtroSeleccionado == valor,
                            onClick = {
                                filtroSeleccionado = valor
                            },
                            label = {
                                Text(titulo)
                            }
                        )
                    }
                }

                Text(
                    text = "${registrosFiltrados.size} actividades",
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )

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
