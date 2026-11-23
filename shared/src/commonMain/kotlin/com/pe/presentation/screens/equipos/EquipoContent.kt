
package com.pe.presentation.screens.equipos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Equipo
import com.pe.domain.model.DisponibilidadEquipo
import com.pe.domain.model.EstadoFisicoEquipo

import com.pe.presentation.components.Buscador
import com.pe.presentation.components.EquipoCard
import com.pe.presentation.theme.*

data class OpcionAmbiente(
    val idAmbiente: Int,
    val nombre: String
)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun EquipoContent(
    equipos: List<Equipo>,
    ambientes: List<OpcionAmbiente>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    esEncargado: Boolean
) {
    // BÚSQUEDA
    var busqueda by remember {
        mutableStateOf("")
    }

    // FILTROS APLICADOS
    var ambienteSeleccionado by remember {
        mutableStateOf<Int?>(null)
    }

    var estadoSeleccionado by remember {
        mutableStateOf<DisponibilidadEquipo?>(null)
    }

    var estadoFisicoSeleccionado by remember {
        mutableStateOf<EstadoFisicoEquipo?>(null)
    }

    // PANEL INFERIOR
    var mostrarFiltros by remember {
        mutableStateOf(false)
    }

    // FILTROS TEMPORALES
    var borradorAmbiente by remember {
        mutableStateOf<Int?>(null)
    }

    var borradorDisponibilidad by remember {
        mutableStateOf<DisponibilidadEquipo?>(null)
    }

    var borradorEstadoFisico by remember {
        mutableStateOf<EstadoFisicoEquipo?>(null)
    }

    val cantidadFiltrosActivos = listOf(
        ambienteSeleccionado,
        estadoSeleccionado,
        estadoFisicoSeleccionado
    ).count { it != null }

    // FILTRADO DE EQUIPOS YA CARGADOS
    val equiposFiltrados = remember(
        equipos,
        busqueda,
        ambienteSeleccionado,
        estadoSeleccionado,
        estadoFisicoSeleccionado
    ) {
        equipos.filter { equipo ->

            val coincideBusqueda =
                busqueda.isBlank() ||
                        equipo.nombre.contains(
                            busqueda,
                            ignoreCase = true
                        ) ||
                        equipo.codigoQr.contains(
                            busqueda,
                            ignoreCase = true
                        )

            val coincideAmbiente =
                ambienteSeleccionado == null ||
                        equipo.idAmbiente == ambienteSeleccionado

            val coincideDisponibilidad =
                estadoSeleccionado == null ||
                        equipo.disponibilidad == estadoSeleccionado

            val coincideEstadoFisico =
                estadoFisicoSeleccionado == null ||
                        equipo.estadoFisico == estadoFisicoSeleccionado

            coincideBusqueda &&
                    coincideAmbiente &&
                    coincideDisponibilidad &&
                    coincideEstadoFisico
        }
    }

    // VENTANA INFERIOR DE FILTROS
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Filtros",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextDark
                    )

                    TextButton(
                        onClick = {
                            borradorAmbiente = null
                            borradorDisponibilidad = null
                            borradorEstadoFisico = null
                        }
                    ) {
                        Text("Limpiar")
                    }
                }

                // DISPONIBILIDAD
                Text(
                    text = "Disponibilidad",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = borradorDisponibilidad == null,
                        onClick = {
                            borradorDisponibilidad = null
                        },
                        label = { Text("Todas") }
                    )

                    DisponibilidadEquipo.entries.forEach { estado ->
                        FilterChip(
                            selected = borradorDisponibilidad == estado,
                            onClick = {
                                borradorDisponibilidad = estado
                            },
                            label = {
                                Text(estado.etiqueta)
                            }
                        )
                    }
                }

                // AMBIENTES REALES
                Text(
                    text = "Ambiente",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = borradorAmbiente == null,
                        onClick = {
                            borradorAmbiente = null
                        },
                        label = { Text("Todos") }
                    )

                    ambientes.forEach { ambiente ->
                        FilterChip(
                            selected =
                                borradorAmbiente == ambiente.idAmbiente,
                            onClick = {
                                borradorAmbiente = ambiente.idAmbiente
                            },
                            label = {
                                Text(ambiente.nombre)
                            }
                        )
                    }
                }

                if (ambientes.isEmpty()) {
                    Text(
                        text = "No hay ambientes disponibles.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // ESTADO FÍSICO
                Text(
                    text = "Estado físico",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = borradorEstadoFisico == null,
                        onClick = {
                            borradorEstadoFisico = null
                        },
                        label = { Text("Todos") }
                    )

                    EstadoFisicoEquipo.entries.forEach { estado ->
                        FilterChip(
                            selected = borradorEstadoFisico == estado,
                            onClick = {
                                borradorEstadoFisico = estado
                            },
                            label = {
                                Text(estado.etiqueta)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        ambienteSeleccionado = borradorAmbiente
                        estadoSeleccionado = borradorDisponibilidad
                        estadoFisicoSeleccionado = borradorEstadoFisico

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

    // PANTALLA PRINCIPAL
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // BUSCADOR + BOTÓN DE FILTROS
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Buscador(
                texto = busqueda,
                onValueChange = {
                    busqueda = it
                },
                modifier = Modifier.weight(1f),
                placeholder = "Buscar equipo..."
            )

            BadgedBox(
                badge = {
                    if (cantidadFiltrosActivos > 0) {
                        Badge {
                            Text(cantidadFiltrosActivos.toString())
                        }
                    }
                }
            ) {
                OutlinedIconButton(
                    onClick = {
                        // Copiar selección actual
                        borradorAmbiente = ambienteSeleccionado
                        borradorDisponibilidad = estadoSeleccionado
                        borradorEstadoFisico =
                            estadoFisicoSeleccionado

                        mostrarFiltros = true
                    },
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Abrir filtros",
                        tint = if (cantidadFiltrosActivos > 0) {
                            Primary
                        } else {
                            TextDark
                        }
                    )
                }
            }
        }

        // FILTROS RÁPIDOS DE DISPONIBILIDAD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = estadoSeleccionado == null,
                onClick = {
                    estadoSeleccionado = null
                },
                label = {
                    Text("Todos")
                }
            )

            DisponibilidadEquipo.entries.forEach { estado ->
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

        // LISTA
        if (equiposFiltrados.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron equipos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    bottom = 96.dp
                )
            ) {
                items(
                    items = equiposFiltrados,
                    key = { it.idEquipo }
                ) { equipo ->

                    EquipoCard(
                        equipo = equipo,
                        mostrarCodigo = esEncargado,
                        onClick = onClick
                    )
                }
            }
        }
    }
}
