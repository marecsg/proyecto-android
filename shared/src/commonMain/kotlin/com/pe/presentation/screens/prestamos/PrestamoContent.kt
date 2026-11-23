
package com.pe.presentation.screens.prestamos

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.EstadoSolicitud
import com.pe.domain.model.Prestamo
import com.pe.domain.model.Solicitud
import com.pe.presentation.components.Buscador
import com.pe.presentation.components.PrestamoCard
import com.pe.presentation.theme.*

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
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

    var estadoSeleccionado by remember {
        mutableStateOf<String?>(null)
    }

    var ambienteSeleccionado by remember {
        mutableStateOf<String?>(null)
    }

    // Panel inferior
    var mostrarFiltros by remember {
        mutableStateOf(false)
    }

    var borradorEstado by remember {
        mutableStateOf<String?>(null)
    }

    var borradorAmbiente by remember {
        mutableStateOf<String?>(null)
    }

    val estadosDisponibles = if (pestanaSeleccionada == 0) {
        EstadoSolicitud.entries.map { it.name to it.etiqueta }
    } else {
        EstadoPrestamo.entries.map { it.name to it.etiqueta }
    }

    val ambientesDisponibles = (
            if (pestanaSeleccionada == 0) {
                solicitudes.map { it.nombreAmbiente }
            } else {
                prestamos.map { it.nombreAmbiente }
            }
            ).filter { it.isNotBlank() }.distinct().sorted()

    val filtrosActivos =
        (if (estadoSeleccionado != null) 1 else 0) +
                (if (ambienteSeleccionado != null) 1 else 0)

    // Filtros locales sobre los registros cargados
    val solicitudesFiltradas = solicitudes.filter {
        (estadoSeleccionado == null ||
                it.estado.name == estadoSeleccionado) &&
                (ambienteSeleccionado == null ||
                        it.nombreAmbiente == ambienteSeleccionado)
    }

    val prestamosFiltrados = prestamos.filter {
        (estadoSeleccionado == null ||
                it.estado.name == estadoSeleccionado) &&
                (ambienteSeleccionado == null ||
                        it.nombreAmbiente == ambienteSeleccionado)
    }.sortedByDescending { it.idPrestamo }



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
                        "Filtros",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextDark
                    )

                    TextButton(
                        onClick = {
                            borradorEstado = null
                            borradorAmbiente = null
                        }
                    ) {
                        Text("Limpiar")
                    }
                }

                Text(
                    "Estado",
                    style = MaterialTheme.typography.titleMedium
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = borradorEstado == null,
                        onClick = { borradorEstado = null },
                        label = { Text("Todos") }
                    )

                    estadosDisponibles.forEach { (valor, etiqueta) ->
                        FilterChip(
                            selected = borradorEstado == valor,
                            onClick = { borradorEstado = valor },
                            label = { Text(etiqueta) }
                        )
                    }
                }

                Text(
                    "Ambiente",
                    style = MaterialTheme.typography.titleMedium
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = borradorAmbiente == null,
                        onClick = { borradorAmbiente = null },
                        label = { Text("Todos") }
                    )

                    ambientesDisponibles.forEach { ambiente ->
                        FilterChip(
                            selected = borradorAmbiente == ambiente,
                            onClick = {
                                borradorAmbiente = ambiente
                            },
                            label = { Text(ambiente) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        estadoSeleccionado = borradorEstado
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


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundClean)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Buscador(
                texto = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.weight(1f),
                placeholder = if (pestanaSeleccionada == 0) {
                    "Buscar solicitudes..."
                } else {
                    "Buscar préstamos..."
                }
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
                        borradorAmbiente = ambienteSeleccionado
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
                    estadoSeleccionado = null
                    ambienteSeleccionado = null
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
                    estadoSeleccionado = null
                    ambienteSeleccionado = null
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CONTENIDO SEGÚN PESTAÑA
        when (pestanaSeleccionada) {

            0 -> {

                SolicitudesContent(
                    solicitudes = solicitudesFiltradas,
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
                    prestamos = prestamosFiltrados,
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


                    items(
                        items = prestamosFiltrados,
                        key = { it.idPrestamo }
                    ) { prestamo ->

                        PrestamoCard(
                            prestamo = prestamo,
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
