
package com.pe.presentation.screens.prestamos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.pe.domain.model.Equipo
import com.pe.presentation.components.BotonPrincipal
import com.pe.presentation.theme.*
import com.pe.utils.FechaUtils
import com.pe.utils.HoraVisualTransformation
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearSolicitudContent(
    equiposSeleccionados: List<Equipo>,
    procesando: Boolean = false,
    error: String? = null,
    onVolver: () -> Unit,
    onElegirDeLista: () -> Unit,
    onEscanearQr: () -> Unit,
    onQuitarEquipo: (Int) -> Unit,
    onEnviarSolicitud: (String, String, String, Boolean) -> Unit
) {
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var periodoInicio by remember { mutableStateOf("AM") }
    var periodoFin by remember { mutableStateOf("AM") }
    var motivo by remember { mutableStateOf("") }
    var aceptoTerminos by remember {
        mutableStateOf(false)
    }


    var mostrarOpciones by remember { mutableStateOf(false) }
    var seleccionandoFecha by remember { mutableStateOf<Boolean?>(null) }

    // Calendario para elegir fechas
    if (seleccionandoFecha != null) {
        val pickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { seleccionandoFecha = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val fecha = LocalDate.fromEpochDays(
                                (millis / 86_400_000L).toInt()
                            ).toString()

                            if (seleccionandoFecha == true) {
                                fechaInicio = fecha
                            } else {
                                fechaFin = fecha
                            }
                        }
                        seleccionandoFecha = null
                    },
                    enabled = pickerState.selectedDateMillis != null
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { seleccionandoFecha = null }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    // Ventanita para agregar equipos
    if (mostrarOpciones) {
        ModalBottomSheet(
            onDismissRequest = { mostrarOpciones = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Agregar equipo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                BotonPrincipal(
                    texto = "Escanear código QR",
                    onClick = {
                        mostrarOpciones = false
                        onEscanearQr()
                    }
                )

                BotonPrincipal(
                    texto = "Elegir de la lista",
                    onClick = {
                        mostrarOpciones = false
                        onElegirDeLista()
                    },
                    secundario = true
                )

                TextButton(
                    onClick = { mostrarOpciones = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }

    val horaInicio24 = FechaUtils.convertirHora24(
        horaInicio,
        periodoInicio
    )

    val horaFin24 = FechaUtils.convertirHora24(
        horaFin,
        periodoFin
    )

    val inicio = if (fechaInicio.isNotBlank() && horaInicio24 != null) {
        "${fechaInicio}T${horaInicio24}:00"
    } else null

    val fin = if (fechaFin.isNotBlank() && horaFin24 != null) {
        "${fechaFin}T${horaFin24}:00"
    } else null

    val fechasValidas = inicio != null &&
            fin != null && fin > inicio

    val equiposValidos =
        equiposSeleccionados.isNotEmpty() &&
        equiposSeleccionados.all { it.estaDisponible } &&
        equiposSeleccionados.map { it.idAmbiente }.distinct().size == 1

    val formularioValido =
        equiposValidos &&
                fechasValidas &&
                motivo.isNotBlank() &&
                aceptoTerminos


    val mensajeValidacion = when {
        equiposSeleccionados.isEmpty() ->
            "Agrega al menos un equipo."

        !equiposValidos ->
            "Selecciona equipos disponibles del mismo laboratorio."

        fechaInicio.isBlank() || fechaFin.isBlank() ->
            "Selecciona las fechas de inicio y devolución."

        horaInicio24 == null || horaFin24 == null ->
            "Ingresa horas válidas, por ejemplo 08:00 y 10:00."

        !fechasValidas ->
            "La devolución debe ser posterior al inicio."

        motivo.isBlank() ->
            "Escribe el motivo de tu solicitud."

        !aceptoTerminos ->
            "Acepta los términos y condiciones."

        else -> null
    }


    Scaffold(
        containerColor = BackgroundClean,
        topBar = {
            TopAppBar(
                title = { Text("Solicitar préstamo") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BotonPrincipal(
                    texto = if (procesando)
                        "Enviando..." else "Enviar solicitud",
                    habilitado = formularioValido && !procesando,
                    onClick = {
                        if (formularioValido &&
                            inicio != null && fin != null
                        ) {
                            onEnviarSolicitud(
                                inicio,
                                fin,
                                motivo.trim(),
                                aceptoTerminos
                            )
                        }
                    }
                )
                if (mensajeValidacion != null && !procesando) {
                    Text(
                        text = mensajeValidacion,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                TextButton(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // EQUIPOS SELECCIONADOS
            Text(
                "Equipos (${equiposSeleccionados.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (equiposSeleccionados.isEmpty()) {
                        Text(
                            "Todavía no agregaste equipos.",
                            color = TextMuted
                        )
                    }

                    equiposSeleccionados.forEach { equipo ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    equipo.nombre,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${equipo.codigoQr} · ${equipo.nombreAmbiente}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }

                            IconButton(
                                onClick = {
                                    onQuitarEquipo(equipo.idEquipo)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Quitar equipo"
                                )
                            }
                        }

                        HorizontalDivider()
                    }

                    TextButton(
                        onClick = { mostrarOpciones = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Agregar otro equipo")
                    }
                }
            }

            if (!equiposValidos && equiposSeleccionados.isNotEmpty()) {
                Text(
                    "Selecciona equipos disponibles del mismo ambiente.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // FECHAS Y HORAS
            Text(
                "Fecha y hora",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fecha de préstamo")
                    OutlinedButton(
                        onClick = { seleccionandoFecha = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(mostrarFecha(fechaInicio))
                    }
                }


                Column(modifier = Modifier.weight(1f)) {
                    Text("Hora de inicio")
                    OutlinedTextField(
                        value = horaInicio,
                        onValueChange = {
                            horaInicio = formatearHora(it)
                        },
                        placeholder = { Text("08:00") },
                        singleLine = true,
                        visualTransformation = HoraVisualTransformation,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("AM", "PM").forEach { periodo ->
                            FilterChip(
                                selected = periodoInicio == periodo,
                                onClick = { periodoInicio = periodo },
                                label = { Text(periodo) }
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fecha de devolución")
                    OutlinedButton(
                        onClick = { seleccionandoFecha = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(mostrarFecha(fechaFin))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Hora de devolución")
                    OutlinedTextField(
                        value = horaFin,
                        onValueChange = {
                            horaFin = formatearHora(it)
                        },
                        placeholder = { Text("10:00") },
                        singleLine = true,
                        visualTransformation = HoraVisualTransformation,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("AM", "PM").forEach { periodo ->
                            FilterChip(
                                selected = periodoFin == periodo,
                                onClick = { periodoFin = periodo },
                                label = { Text(periodo) }
                            )
                        }
                    }
                }
            }

            if (inicio != null && fin != null && !fechasValidas) {
                Text(
                    "La devolución debe ser posterior al inicio.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // MOTIVO
            Text(
                "Motivo del préstamo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("¿Para qué necesitas los equipos?")
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = aceptoTerminos,
                    onCheckedChange = { aceptoTerminos = it }
                )

                Text(
                    text = "He leído y acepto los términos y condiciones del préstamo de equipos.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (error != null) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatearHora(texto: String): String {
   return texto.filter { it.isDigit() }.take(4)
}

private fun mostrarFecha(fecha: String): String {
    if (fecha.isBlank()) return "Elegir fecha"

    val partes = fecha.split("-")
    return if (partes.size == 3) {
        "${partes[2]}/${partes[1]}/${partes[0]}"
    } else {
        fecha
    }
}
