
package com.pe.presentation.screens.equipos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Equipo
import com.pe.domain.model.DisponibilidadEquipo

import com.pe.presentation.components.Buscador
import com.pe.presentation.components.EquipoCard
import com.pe.presentation.theme.*

// Representa una opción del selector de ambientes
data class OpcionAmbiente(
    val idAmbiente: Int,
    val nombre: String
)

@Composable
fun EquipoContent(
    equipos: List<Equipo>,
    ambientes: List<OpcionAmbiente>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    esEncargado: Boolean
) {
    var busqueda by remember {
        mutableStateOf("")
    }

    var ambienteSeleccionado by remember {
        mutableStateOf<Int?>(null)
    }

    var estadoSeleccionado by remember {
        mutableStateOf<DisponibilidadEquipo?>(null)
    }

    var menuExpandido by remember {
        mutableStateOf(false)
    }

    // Filtrado local de los equipos recibidos
    val equiposFiltrados = remember(
        equipos,
        ambienteSeleccionado,
        estadoSeleccionado,
        busqueda
    ) {
        equipos.filter { equipo ->

            val coincideAmbiente =
                ambienteSeleccionado == null ||
                equipo.idAmbiente == ambienteSeleccionado

            val coincideEstado =
                estadoSeleccionado == null ||
                equipo.disponibilidad == estadoSeleccionado

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

            coincideAmbiente &&
                coincideEstado &&
                coincideBusqueda
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // BUSCADOR
        Buscador(
            texto = busqueda,
            onValueChange = { busqueda = it },
            placeholder = "Buscar equipo..."
        )

        // SELECTOR DE AMBIENTES
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Ambiente",
                style = MaterialTheme.typography.labelMedium,
                color = TextBody
            )

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        menuExpandido = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceWhite,
                        contentColor = TextDark
                    )
                ) {
                    Text(
                        text = ambientes
                            .firstOrNull {
                                it.idAmbiente == ambienteSeleccionado
                            }
                            ?.nombre ?: if (ambientes.isEmpty()) {
                            "No hay ambientes disponibles"
                        } else {
                            "Todos los ambientes"
                        }
                    )
                }


                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = {
                        menuExpandido = false
                    }
                ) {
                    if (ambientes.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "No hay ambientes disponibles",
                                    color = TextMuted
                                )
                            },
                            onClick = {},
                            enabled = false
                        )
                    } else {
                        DropdownMenuItem(
                            text = {
                                Text("Todos los ambientes")
                            },
                            onClick = {
                                ambienteSeleccionado = null
                                menuExpandido = false
                            }
                        )

                        ambientes.forEach { ambiente ->
                            DropdownMenuItem(
                                text = {
                                    Text(ambiente.nombre)
                                },
                                onClick = {
                                    ambienteSeleccionado = ambiente.idAmbiente
                                    menuExpandido = false
                                }
                            )
                        }
                    }
                }

            }
        }

        // FILTROS DE ESTADO
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

        // LISTA DE EQUIPOS
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
                    bottom = 16.dp
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
