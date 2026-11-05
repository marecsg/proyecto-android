package com.pe.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pe.domain.model.Equipment
import com.pe.domain.model.IncidentDraft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportContent(
    equipmentList: List<Equipment>,
    onBack: () -> Unit,
    onConfirm: (IncidentDraft) -> Unit
) {

    var selectedEquipment by remember {
        mutableStateOf(
            equipmentList.first()
        )
    }

    val incidentTypes = listOf(
        "Falla de hardware",
        "Daño físico",
        "Problema de conexión",
        "Equipo incompleto",
        "Otro"
    )

    var selectedType by remember {
        mutableStateOf(
            incidentTypes.first()
        )
    }

    var description by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf("")
    }

    val formIsValid =
        description.isNotBlank() &&
                date.isNotBlank()

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Reportar incidencia")
                },
                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Text(
                text = "Equipo afectado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            equipmentList.forEach { equipment ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedEquipment = equipment
                        }
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected = selectedEquipment.id == equipment.id,
                            onClick = {
                                selectedEquipment = equipment
                            }
                        )

                        Column(
                            modifier = Modifier.padding(start = 8.dp)
                        ) {

                            Text(
                                text = equipment.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "${equipment.id} • ${equipment.laboratory}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Text(
                text = "Tipo de incidencia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            incidentTypes.forEach { type ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedType = type
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                        }
                    )

                    Text(
                        text = type,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Descripción del problema")
                },
                placeholder = {
                    Text(
                        "Ejemplo: El equipo no enciende correctamente."
                    )
                },
                minLines = 4
            )

            OutlinedTextField(
                value = date,
                onValueChange = {
                    date = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Fecha")
                },
                singleLine = true
            )

            Button(
                onClick = {

                    onConfirm(
                        IncidentDraft(
                            equipment = selectedEquipment,
                            type = selectedType,
                            description = description,
                            date = date
                        )
                    )
                },
                enabled = formIsValid,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Registrar incidencia"
                )
            }
        }
    }
}