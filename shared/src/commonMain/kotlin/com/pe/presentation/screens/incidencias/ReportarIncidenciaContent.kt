
package com.pe.presentation.screens.incidencias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Equipo
import com.pe.domain.model.TipoIncidencia
import com.pe.presentation.screens.equipos.OpcionAmbiente
import com.pe.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportarIncidenciaContent(
    ambientes: List<OpcionAmbiente>,
    equipos: List<Equipo>,
    onVolver: () -> Unit,
    onRegistrar: (Int, Int?, TipoIncidencia, String) -> Unit,
    procesando: Boolean = false,
    error: String? = null
) {

    var idAmbiente by remember {
        mutableStateOf<Int?>(null)
    }

    var idEquipo by remember {
        mutableStateOf<Int?>(null)
    }

    var tipoSeleccionado by remember {
        mutableStateOf<TipoIncidencia?>(null)
    }

    var problema by remember {
        mutableStateOf<String?>(null)
    }

    var descripcion by remember {
        mutableStateOf("")
    }

    var menuAmbientes by remember {
        mutableStateOf(false)
    }

    var menuEquipos by remember {
        mutableStateOf(false)
    }

    val equiposDelAmbiente = equipos.filter {
        it.idAmbiente == idAmbiente
    }

    val ambienteElegido = ambientes.firstOrNull {
        it.idAmbiente == idAmbiente
    }

    val equipoElegido = equiposDelAmbiente.firstOrNull {
        it.idEquipo == idEquipo
    }

    val problemas = listOf(
        "No enciende",
        "Daño físico",
        "Falla de funcionamiento"
    )

    val formularioValido =
        ambienteElegido != null &&
                tipoSeleccionado != null &&
                descripcion.isNotBlank()

    Scaffold(
        containerColor = BackgroundClean,

        topBar = {
            TopAppBar(
                title = {
                    Text("Reportar incidencia")
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(16.dp)
            ) {

                Button(
                    onClick = {
                        val ambiente = ambienteElegido
                        val tipo = tipoSeleccionado

                        if (ambiente != null &&
                            tipo != null &&
                            formularioValido
                        ) {
                            val descripcionFinal =
                                buildString {
                                    if (problema != null) {
                                        append("Problema: ")
                                        append(problema)
                                        append("\n")
                                    }
                                    append(descripcion.trim())
                                }

                            onRegistrar(
                                ambiente.idAmbiente,
                                equipoElegido?.idEquipo,
                                tipo,
                                descripcionFinal
                            )
                        }
                    },
                    enabled = formularioValido && !procesando,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (procesando) "Enviando..."
                        else "Registrar incidencia"
                    )
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

            Text(
                "Completa los datos de la incidencia",
                style = MaterialTheme.typography.titleMedium,
                color = TextDark
            )

            // SELECCIONAR LABORATORIO
            Text(
                "Laboratorio *",
                style = MaterialTheme.typography.titleSmall
            )

            Box {
                OutlinedButton(
                    onClick = { menuAmbientes = true },
                    enabled = !procesando &&
                            ambientes.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        ambienteElegido?.nombre
                            ?: "Seleccionar laboratorio"
                    )
                }

                DropdownMenu(
                    expanded = menuAmbientes,
                    onDismissRequest = {
                        menuAmbientes = false
                    }
                ) {
                    ambientes.forEach { ambiente ->
                        DropdownMenuItem(
                            text = { Text(ambiente.nombre) },
                            onClick = {
                                idAmbiente = ambiente.idAmbiente
                                idEquipo = null
                                menuAmbientes = false
                            }
                        )
                    }
                }
            }

            // SELECCIONAR EQUIPO
            Text(
                "Equipo (opcional)",
                style = MaterialTheme.typography.titleSmall
            )

            Box {
                OutlinedButton(
                    onClick = { menuEquipos = true },
                    enabled = idAmbiente != null &&
                            !procesando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        equipoElegido?.nombre
                            ?: "Sin equipo asociado"
                    )
                }

                DropdownMenu(
                    expanded = menuEquipos,
                    onDismissRequest = {
                        menuEquipos = false
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sin equipo asociado") },
                        onClick = {
                            idEquipo = null
                            menuEquipos = false
                        }
                    )

                    equiposDelAmbiente.forEach { equipo ->
                        DropdownMenuItem(
                            text = { Text(equipo.nombre) },
                            onClick = {
                                idEquipo = equipo.idEquipo
                                menuEquipos = false
                            }
                        )
                    }
                }
            }

            // TIPO DE INCIDENCIA
            Text(
                "Tipo de incidencia *",
                style = MaterialTheme.typography.titleSmall
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TipoIncidencia.entries.forEach { tipo ->
                    Row(
                        verticalAlignment =
                            androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tipoSeleccionado == tipo,
                            onClick = {
                                tipoSeleccionado = tipo
                            },
                            enabled = !procesando
                        )

                        Text(
                            text = tipo.etiqueta,
                            color = TextBody
                        )
                    }
                }
            }

            // PROBLEMA ESPECIFICO
            Text(
                "Problema observado (opcional)",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                "Selecciona una opción si corresponde.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Column {
                problemas.forEach { opcion ->
                    FilterChip(
                        selected = problema == opcion,
                        onClick = {
                            problema = if (problema == opcion) {
                                null
                            } else {
                                opcion
                            }
                        },
                        label = { Text(opcion) },
                        enabled = !procesando
                    )
                }
            }

            // DESCRIPCION
            Text(
                "Descripción del problema *",
                style = MaterialTheme.typography.titleSmall
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Describe qué ocurrió y dónde..."
                    )
                },
                minLines = 4,
                maxLines = 6,
                enabled = !procesando,
                shape = RoundedCornerShape(12.dp)
            )

            // FOTOGRAFIAS
            Text(
                "Fotografías",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                "La carga de fotografías todavía " +
                        "no está disponible en el backend.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
