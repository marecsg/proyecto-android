
package com.pe.presentation.screens.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.pe.presentation.components.HistoryCard
import com.pe.presentation.theme.*
import com.pe.presentation.viewmodel.HistorialUiState

@Composable
fun HistoryContent(
    estado: HistorialUiState,
    esEncargado: Boolean,
    onRecargar: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundClean)
    ) {

        // ENCABEZADO DEL HISTORIAL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Historial de actividades",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                Text(
                    text = if (esEncargado) {
                        "Acciones realizadas por ti"
                    } else {
                        "Movimientos relacionados contigo"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

            // Actualizar los registros
            if (
                estado is HistorialUiState.Success ||
                estado is HistorialUiState.Empty
            ) {
                TextButton(
                    onClick = onRecargar
                ) {
                    Text("Actualizar")
                }
            }
        }

        // CONTENIDO SEGÚN EL ESTADO
        when (estado) {

            // ESTADO INICIAL
            HistorialUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Preparando historial...",
                        color = TextMuted
                    )
                }
            }

            // CARGANDO
            HistorialUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Primary
                    )
                }
            }

            // SIN ACTIVIDADES
            HistorialUiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Todavía no tienes actividades registradas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ERROR DE CONSULTA
            is HistorialUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "No se pudo cargar el historial",
                        style = MaterialTheme.typography.titleMedium,
                        color = ErrorText
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = estado.mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onRecargar
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            // LISTA DE ACTIVIDADES REALES
            is HistorialUiState.Success -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        Text(
                            text = "${estado.historial.size} actividades registradas",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMuted
                        )
                    }

                    items(
                        items = estado.historial,
                        key = { it.idHistorial }
                    ) { registro ->

                        HistoryCard(
                            historial = registro
                        )
                    }
                }
            }
        }
    }
}
