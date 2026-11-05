package com.pe.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pe.presentation.components.IncidentCard
import com.pe.domain.model.Incident

@Composable
fun IncidentsContent(
    incidents: List<Incident>,
    onReportIncident: () -> Unit,
    onIncidentClick: (Incident) -> Unit
) {

    if (incidents.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(28.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "No hay incidencias registradas",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Si encuentras un problema en algún equipo, puedes reportarlo aquí.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onReportIncident,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Reportar incidencia"
                    )
                }
            }
        }

        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "Incidencias",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Consulta los problemas reportados en los equipos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onReportIncident,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {

                    Text(
                        text = "Nueva incidencia"
                    )
                }
            }
        }

        items(
            items = incidents,
            key = {
                it.id
            }
        ) { incident ->

            IncidentCard(
                incident = incident,
                onClick = {
                    onIncidentClick(incident)
                }
            )
        }
    }
}