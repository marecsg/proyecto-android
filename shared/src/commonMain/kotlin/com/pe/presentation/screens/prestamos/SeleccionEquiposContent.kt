
package com.pe.presentation.screens.prestamos

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
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

@OptIn(ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class)
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


    var mostrarFiltros by remember {
        mutableStateOf(false)
    }

    var borradorFiltro by remember {
        mutableStateOf("Todos")
    }

    var ambienteSeleccionado by remember {
        mutableStateOf<Int?>(null)
    }

    var borradorAmbiente by remember {
        mutableStateOf<Int?>(null)
    }

    val ambientesDisponibles = equipos
        .distinctBy { it.idAmbiente }
        .sortedBy { it.nombreAmbiente }

    val filtrosActivos =
        (if (filtro != "Todos") 1 else 0) +
                (if (ambienteSeleccionado != null) 1 else 0)


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

        val coincideAmbiente =
            ambienteSeleccionado == null ||
                    equipo.idAmbiente == ambienteSeleccionado

        coincideBusqueda && coincideFiltro && coincideAmbiente
    }


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
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Filtros",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextDark
                    )

                    TextButton(
                        onClick = {
                            borradorFiltro = "Todos"
                            borradorAmbiente = null
                        }
                    ) {
                        Text("Limpiar")
                    }
                }

                Text(
                    "Disponibilidad",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Todos",
                        "Disponibles",
                        "Prestados",
                        "Mantenimiento"
                    ).forEach { opcion ->
                        FilterChip(
                            selected = borradorFiltro == opcion,
                            onClick = {
                                borradorFiltro = opcion
                            },
                            label = {
                                Text(opcion)
                            }
                        )
                    }
                }

                Text(
                    "Ambiente",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )

                FlowRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = borradorAmbiente == null,
                        onClick = {
                            borradorAmbiente = null
                        },
                        label = {
                            Text("Todos")
                        }
                    )

                    ambientesDisponibles.forEach { ambiente ->
                        FilterChip(
                            selected =
                                borradorAmbiente == ambiente.idAmbiente,
                            onClick = {
                                borradorAmbiente = ambiente.idAmbiente
                            },
                            label = {
                                Text(ambiente.nombreAmbiente)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        filtro = borradorFiltro
                        ambienteSeleccionado = borradorAmbiente
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


            // BUSCADOR + BOTÓN DE FILTROS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
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
                    placeholder = "Buscar equipo o código"
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
                            borradorFiltro = filtro
                            borradorAmbiente = ambienteSeleccionado
                            mostrarFiltros = true
                        },
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Abrir filtros",
                            tint = if (filtrosActivos > 0) {
                                Primary
                            } else {
                                TextDark
                            }
                        )
                    }
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
