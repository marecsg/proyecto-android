package com.pe.presentation.screens.prestamos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pe.domain.model.EstadoFisicoEquipo
import com.pe.domain.model.EstadoItem
import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.Prestamo
import com.pe.domain.repository.PrestamoRepository.DevolucionItem
import com.pe.presentation.components.BotonPrincipal
import com.pe.presentation.theme.*

private data class EvaluacionDevolucion(
    val estadoItem: EstadoItem? = null,
    val estadoFisico: EstadoFisicoEquipo? = null,
    val comentario: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarDevolucionContent(
    prestamo: Prestamo,
    onVolver: () -> Unit,
    onConfirmar: (List<DevolucionItem>) -> Unit,
    procesando: Boolean = false,
    error: String? = null
) {
    var evaluaciones by remember(prestamo.idPrestamo) {
        mutableStateOf<Map<Int, EvaluacionDevolucion>>(emptyMap())
    }

    var mostrarConfirmacion by remember(prestamo.idPrestamo) {
        mutableStateOf(false)
    }

    val devueltos = prestamo.detalles.count {
        evaluaciones[it.idDetalle]?.estadoItem == EstadoItem.DEVUELTO
    }

    val noDevueltos = prestamo.detalles.count {
        evaluaciones[it.idDetalle]?.estadoItem == EstadoItem.NO_DEVUELTO
    }

    val evaluados = devueltos + noDevueltos

    val listo = prestamo.estado == EstadoPrestamo.EN_CURSO &&
            prestamo.detalles.isNotEmpty() &&
            prestamo.detalles.all { detalle ->
                val e = evaluaciones[detalle.idDetalle]
                e?.estadoItem != null &&
                        (e.estadoItem == EstadoItem.NO_DEVUELTO || e.estadoFisico != null)
            }

    fun actualizar(
        idDetalle: Int,
        evaluacion: EvaluacionDevolucion
    ) {
        evaluaciones = evaluaciones + (idDetalle to evaluacion)
    }

    // CONFIRMACIÓN FINAL
    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = {
                if (!procesando) {
                    mostrarConfirmacion = false
                }
            },
            title = {
                Text("Confirmar devolución")
            },
            text = {
                Text(
                    "Se registrarán $devueltos equipo(s) devuelto(s) y " +
                            "$noDevueltos no devuelto(s). " +
                            "El préstamo quedará finalizado."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = listo && !procesando,
                    onClick = {
                        mostrarConfirmacion = false

                        val datos = prestamo.detalles.map { detalle ->
                            val e = requireNotNull(
                                evaluaciones[detalle.idDetalle]
                            )

                            DevolucionItem(
                                idDetalle = detalle.idDetalle,
                                estadoItem = requireNotNull(e.estadoItem),
                                estadoFisicoAlDevolver =
                                    if (e.estadoItem == EstadoItem.DEVUELTO) {
                                        e.estadoFisico
                                    } else {
                                        null
                                    },
                                comentarioDevolucion = e.comentario
                                    .trim()
                                    .ifBlank { null }
                            )
                        }

                        onConfirmar(datos)
                    }
                ) {
                    Text("Sí, registrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundClean,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Registrar devolución",
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onVolver,
                        enabled = !procesando
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
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
            Surface(
                color = SurfaceWhite,
                shadowElevation = 5.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 14.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!listo) {
                        Text(
                            text = if (
                                prestamo.estado != EstadoPrestamo.EN_CURSO
                            ) {
                                "Solo se puede devolver un préstamo en curso."
                            } else {
                                "Completa el estado y, si fue devuelto, " +
                                        "la condición física de cada equipo."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }

                    BotonPrincipal(
                        texto = if (procesando) {
                            "Registrando..."
                        } else {
                            "Confirmar devolución"
                        },
                        habilitado = listo && !procesando,
                        onClick = {
                            mostrarConfirmacion = true
                        }
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),

            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 6.dp,
                bottom = 24.dp
            ),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // DATOS DEL ESTUDIANTE
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceWhite
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    PrimaryContainer,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = prestamo.nombreEstudiante
                                    .split(" ")
                                    .filter { it.isNotBlank() }
                                    .take(2)
                                    .mapNotNull {
                                        it.firstOrNull()
                                            ?.uppercaseChar()
                                            ?.toString()
                                    }
                                    .joinToString("")
                                    .ifBlank { "E" },

                                color = Primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                prestamo.nombreEstudiante,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextDark
                            )

                            Text(
                                text = "Cód. ${prestamo.codigoEstudiante.ifBlank { "--" }} · ${
                                    prestamo.codigoCorrelativo?.takeIf { it.isNotBlank() } ?: "----"
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            // INDICACIONES
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        "Marca el estado de cada equipo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )

                    Text(
                        "La devolución se registra en bloque " +
                                "para todo el préstamo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            // EVALUAR CADA EQUIPO
            items(
                items = prestamo.detalles,
                key = { it.idDetalle }
            ) { detalle ->

                val e = evaluaciones[detalle.idDetalle]
                    ?: EvaluacionDevolucion()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceWhite
                    ),

                    border = BorderStroke(
                        1.dp,
                        if (e.estadoItem == EstadoItem.NO_DEVUELTO) {
                            ErrorBorder
                        } else {
                            Border
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),

                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // INFORMACIÓN DEL EQUIPO
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        PrimarySoft,
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Devices,
                                    contentDescription = null,
                                    tint = Primary
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    detalle.nombreEquipo,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextDark
                                )

                                Text(
                                    "${detalle.codigoQrEquipo} · " +
                                            prestamo.nombreAmbiente,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }

                        // DEVUELTO / NO DEVUELTO
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            EstadoDevolucionBoton(
                                texto = "Devuelto",
                                seleccionado =
                                    e.estadoItem == EstadoItem.DEVUELTO,
                                esDevuelto = true,
                                habilitado = !procesando,
                                modifier = Modifier.weight(1f),

                                onClick = {
                                    actualizar(
                                        detalle.idDetalle,
                                        e.copy(
                                            estadoItem = EstadoItem.DEVUELTO
                                        )
                                    )
                                }
                            )

                            EstadoDevolucionBoton(
                                texto = "No devuelto",
                                seleccionado =
                                    e.estadoItem == EstadoItem.NO_DEVUELTO,
                                esDevuelto = false,
                                habilitado = !procesando,
                                modifier = Modifier.weight(1f),

                                onClick = {
                                    actualizar(
                                        detalle.idDetalle,
                                        e.copy(
                                            estadoItem = EstadoItem.NO_DEVUELTO,
                                            estadoFisico = null
                                        )
                                    )
                                }
                            )
                        }

                        // ESTADO FÍSICO SOLO SI FUE DEVUELTO
                        if (e.estadoItem == EstadoItem.DEVUELTO) {

                            Text(
                                "Estado físico al devolver",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextDark
                            )

                            EstadoFisicoEquipo.entries.forEach {
                                    estadoFisico ->

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected =
                                            e.estadoFisico == estadoFisico,
                                        enabled = !procesando,

                                        onClick = {
                                            actualizar(
                                                detalle.idDetalle,
                                                e.copy(
                                                    estadoFisico = estadoFisico
                                                )
                                            )
                                        }
                                    )

                                    Text(
                                        estadoFisico.etiqueta,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextBody
                                    )
                                }
                            }
                        }

                        // COMENTARIO INDIVIDUAL DEL EQUIPO
                        if (e.estadoItem != null) {
                            OutlinedTextField(
                                value = e.comentario,

                                onValueChange = { texto ->
                                    actualizar(
                                        detalle.idDetalle,
                                        e.copy(
                                            comentario = texto
                                        )
                                    )
                                },

                                enabled = !procesando,
                                modifier = Modifier.fillMaxWidth(),

                                label = {
                                    Text("Observación (opcional)")
                                },

                                placeholder = {
                                    Text(
                                        "Escribe una observación de este equipo"
                                    )
                                },

                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(14.dp)
                            )
                        }
                    }
                }
            }

            // RESUMEN FINAL
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResumenDevolucionChip(
                        "$devueltos devuelto(s)",
                        SuccessContainer,
                        SuccessText
                    )

                    ResumenDevolucionChip(
                        "$noDevueltos no devuelto(s)",
                        ErrorContainer,
                        ErrorText
                    )
                }
            }

            item {
                Text(
                    "$evaluados de ${
                        prestamo.detalles.size
                    } equipos evaluados",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // ERROR DEL BACKEND
            if (!error.isNullOrBlank()) {
                item {
                    Text(
                        error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ErrorText
                    )
                }
            }
        }
    }
}

// BOTÓN DEVUELTO / NO DEVUELTO
@Composable
private fun EstadoDevolucionBoton(
    texto: String,
    seleccionado: Boolean,
    esDevuelto: Boolean,
    habilitado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val fondo = if (!seleccionado) {
        SurfaceWhite
    } else if (esDevuelto) {
        SuccessContainer
    } else {
        ErrorContainer
    }

    val contenido = if (!seleccionado) {
        TextMuted
    } else if (esDevuelto) {
        SuccessText
    } else {
        ErrorText
    }

    val borde = if (!seleccionado) {
        Border
    } else if (esDevuelto) {
        SuccessText
    } else {
        ErrorText
    }

    OutlinedButton(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(14.dp),

        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = fondo,
            contentColor = contenido
        ),

        border = BorderStroke(
            if (seleccionado) 1.5.dp else 1.dp,
            borde
        ),

        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

// CONTADOR DE EQUIPOS EVALUADOS
@Composable
private fun ResumenDevolucionChip(
    texto: String,
    fondo: androidx.compose.ui.graphics.Color,
    contenido: androidx.compose.ui.graphics.Color
) {
    Surface(
        color = fondo,
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            texto,
            color = contenido,
            style = MaterialTheme.typography.labelSmall,

            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
        )
    }
}
