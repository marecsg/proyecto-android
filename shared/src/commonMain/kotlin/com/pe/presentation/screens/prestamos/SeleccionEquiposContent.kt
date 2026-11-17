
package com.pe.presentation.screens.prestamos

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pe.domain.model.DisponibilidadEquipo
import com.pe.domain.model.Equipo
import com.pe.domain.model.EstadoFisicoEquipo
import com.pe.presentation.components.BotonPrincipal
import com.pe.presentation.components.Buscador
import com.pe.presentation.components.EquipoCard
import com.pe.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionEquiposContent(
    equipos: List<Equipo>,
    idsSeleccionados: Set<Int>,
    onSeleccionarEquipo: (Int) -> Unit,
    onContinuar: () -> Unit,
    onVolver: () -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }
    var aviso by remember { mutableStateOf<String?>(null) }

    val seleccionados = equipos.filter {
        it.idEquipo in idsSeleccionados
    }

    val ambienteElegido = seleccionados.firstOrNull()?.idAmbiente

    val equiposFiltrados = equipos.filter { equipo ->
        val coincideBusqueda =
            busqueda.isBlank() ||
            equipo.nombre.contains(busqueda, ignoreCase = true) ||
            equipo.codigoQr.contains(busqueda, ignoreCase = true)

        val coincideFiltro = when (filtro) {
            "Disponibles" -> equipo.estaDisponible
            "Prestados" ->
                equipo.disponibilidad == DisponibilidadEquipo.PRESTADO
            "Mantenimiento" ->
                equipo.estadoFisico == EstadoFisicoEquipo.MANTENIMIENTO
            else -> true
        }

        coincideBusqueda && coincideFiltro
    }

    Scaffold(
        containerColor = BackgroundClean,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "${idsSeleccionados.size} seleccionados",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Elige los equipos que necesitas",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.Default.Close,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${idsSeleccionados.size} equipos",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Para tu solicitud",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                BotonPrincipal(
                    texto = "Continuar →",
                    habilitado = idsSeleccionados.isNotEmpty(),
                    onClick = onContinuar,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Buscador(
                texto = busqueda,
                onValueChange = { busqueda = it },
                placeholder = "Buscar equipo o código"
            )

            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState()
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Todos",
                    "Disponibles",
                    "Prestados",
                    "Mantenimiento"
                ).forEach { opcion ->
                    FilterChip(
                        selected = filtro == opcion,
                        onClick = { filtro = opcion },
                        label = { Text(opcion) }
                    )
                }
            }

            if (aviso != null) {
                Text(
                    text = aviso.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (equiposFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No se encontraron equipos",
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = equiposFiltrados,
                        key = { it.idEquipo }
                    ) { equipo ->

                        EquipoCard(
                            equipo = equipo,
                            seleccionada =
                                equipo.idEquipo in idsSeleccionados,
                            onClick = { idEquipo ->

                                if (idEquipo in idsSeleccionados) {
                                    aviso = null
                                    onSeleccionarEquipo(idEquipo)

                                } else if (!equipo.estaDisponible) {
                                    aviso = "Este equipo no está disponible."

                                } else if (
                                    ambienteElegido != null &&
                                    equipo.idAmbiente != ambienteElegido
                                ) {
                                    aviso = "Los equipos deben pertenecer al mismo ambiente."

                                } else {
                                    aviso = null
                                    onSeleccionarEquipo(idEquipo)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
