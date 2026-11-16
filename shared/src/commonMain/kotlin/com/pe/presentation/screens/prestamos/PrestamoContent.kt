
package com.pe.presentation.screens.prestamos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pe.domain.model.Prestamo
import com.pe.domain.model.Solicitud
import com.pe.presentation.components.PrestamoCard
import com.pe.presentation.screens.solicitudes.SolicitudesContent
import com.pe.presentation.theme.*

@Composable
fun PrestamosContent(
    solicitudes: List<Solicitud>,
    prestamos: List<Prestamo>,
    esEncargado: Boolean,
    onVerDetalleSolicitud: (Int) -> Unit,
    onVerDetallePrestamo: (Int) -> Unit,
    onIniciarPrestamo: (Int) -> Unit,
    onRegistrarDevolucion: (Int) -> Unit,
    modifier: Modifier = Modifier,
    codigosPorEstudiante: Map<Int, String> = emptyMap(),
    cargandoSolicitudes: Boolean = false,
    cargandoPrestamos: Boolean = false,
    errorSolicitudes: String? = null,
    errorPrestamos: String? = null
) {

    var pestanaSeleccionada by rememberSaveable {
        mutableIntStateOf(0)
    }

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundClean)
    ) {

        // BUSCADOR
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
            placeholder = {
                Text(
                    text = if (esEncargado) {
                        "Buscar por nombre o código..."
                    } else {
                        "Buscar por equipo..."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHint
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextMuted
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border
            )
        )

        // PESTAÑAS SOLICITUDES / PRÉSTAMOS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    SurfaceVariant,
                    RoundedCornerShape(14.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            PestanaPrestamos(
                titulo = "Solicitudes",
                cantidad = solicitudes.size,
                seleccionada = pestanaSeleccionada == 0,
                modifier = Modifier.weight(1f),
                onClick = {
                    pestanaSeleccionada = 0
                    busqueda = ""
                }
            )

            PestanaPrestamos(
                titulo = "Préstamos",
                cantidad = prestamos.size,
                seleccionada = pestanaSeleccionada == 1,
                modifier = Modifier.weight(1f),
                onClick = {
                    pestanaSeleccionada = 1
                    busqueda = ""
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CONTENIDO SEGÚN PESTAÑA
        when (pestanaSeleccionada) {

            0 -> {

                SolicitudesContent(
                    solicitudes = solicitudes,
                    busqueda = busqueda,
                    esEncargado = esEncargado,
                    codigosPorEstudiante = codigosPorEstudiante,
                    cargando = cargandoSolicitudes,
                    error = errorSolicitudes,
                    onVerDetalle = onVerDetalleSolicitud,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            1 -> {

                ListaPrestamos(
                    prestamos = prestamos,
                    busqueda = busqueda,
                    esEncargado = esEncargado,
                    cargando = cargandoPrestamos,
                    error = errorPrestamos,
                    onVerDetalle = onVerDetallePrestamo,
                    onIniciarPrestamo = onIniciarPrestamo,
                    onRegistrarDevolucion = onRegistrarDevolucion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

// PESTAÑA REUTILIZABLE DENTRO DE ESTE CONTENEDOR
@Composable
private fun PestanaPrestamos(
    titulo: String,
    cantidad: Int,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(11.dp),
        color = if (seleccionada) {
            SurfaceWhite
        } else {
            SurfaceVariant
        },
        shadowElevation = if (seleccionada) 2.dp else 0.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 11.dp
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = if (seleccionada) Primary else TextMuted,
                fontWeight = if (seleccionada) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
            )

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = if (seleccionada) {
                    Primary
                } else {
                    Border
                }
            ) {
                Text(
                    text = cantidad.toString(),
                    modifier = Modifier.padding(
                        horizontal = 7.dp,
                        vertical = 2.dp
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (seleccionada) {
                        SurfaceWhite
                    } else {
                        TextMuted
                    }
                )
            }
        }
    }
}

// LISTA DE PRÉSTAMOS
@Composable
private fun ListaPrestamos(
    prestamos: List<Prestamo>,
    busqueda: String,
    esEncargado: Boolean,
    cargando: Boolean,
    error: String?,
    onVerDetalle: (Int) -> Unit,
    onIniciarPrestamo: (Int) -> Unit,
    onRegistrarDevolucion: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val consulta = busqueda.trim()

    // FILTRADO EN FRONTEND
    val prestamosFiltrados = prestamos.filter { prestamo ->

        val coincideEquipo = prestamo.detalles.any { equipo ->
            equipo.nombreEquipo.contains(
                consulta,
                ignoreCase = true
            ) || equipo.codigoQrEquipo.contains(
                consulta,
                ignoreCase = true
            )
        }

        val coincideAmbiente = prestamo.nombreAmbiente.contains(
            consulta,
            ignoreCase = true
        )

        val coincideEstado = prestamo.estado.etiqueta.contains(
            consulta,
            ignoreCase = true
        )

        val coincideEstudiante = esEncargado && (
                prestamo.nombreEstudiante.contains(
                    consulta,
                    ignoreCase = true
                ) ||
                        prestamo.codigoEstudiante.contains(
                            consulta,
                            ignoreCase = true
                        )
                )

        consulta.isBlank() ||
                coincideEquipo ||
                coincideAmbiente ||
                coincideEstado ||
                coincideEstudiante
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        when {

            // CARGANDO
            cargando -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    ),
                    color = Primary
                )
            }

            // ERROR
            error != null -> {
                MensajeListaPrestamos(
                    titulo = "Error al cargar préstamos",
                    descripcion = error,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }

            prestamosFiltrados.isEmpty() -> {

                MensajeListaPrestamos(
                    titulo = if (consulta.isBlank()) {
                        "No hay préstamos"
                    } else {
                        "Sin resultados"
                    },
                    descripcion = if (consulta.isNotBlank()) {
                        "No encontramos préstamos que " +
                                "coincidan con tu búsqueda."
                    } else if (esEncargado) {
                        "Todavía no hay préstamos " +
                                "registrados para este ambiente."
                    } else {
                        "Todavía no tienes préstamos registrados."
                    },
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }

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
                        items = prestamosFiltrados,
                        key = { _, prestamo ->
                            prestamo.idPrestamo
                        }
                    ) { _, prestamo ->

                        // POSICIÓN EN LA LISTA ORIGINAL
                        // NO ES EL ID DE BASE DE DATOS
                        val numeroVisualPrestamo =
                            prestamos.indexOfFirst {
                                it.idPrestamo == prestamo.idPrestamo
                            } + 1

                        PrestamoCard(
                            prestamo = prestamo,
                            numero = numeroVisualPrestamo,
                            esEncargado = esEncargado,
                            onClick = onVerDetalle,
                            onIniciarPrestamo = onIniciarPrestamo,
                            onRegistrarDevolucion = onRegistrarDevolucion
                        )
                    }
                }
            }
        }
    }
}

// MENSAJE DE ESTADO VACÍO O ERROR
@Composable
private fun MensajeListaPrestamos(
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
            imageVector = Icons.Default.Inventory2,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}
