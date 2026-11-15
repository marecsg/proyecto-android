package com.pe.presentation.screens.equipos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pe.presentation.components.EquipmentCard
import com.pe.domain.model.Equipment

@Composable
fun EquipmentContent(
    equipment: List<Equipment>,
    onEquipmentClick: (Equipment) -> Unit
) {

    var query by remember {
        mutableStateOf("")
    }

    val filteredEquipment = remember(
        query,
        equipment
    ) {

        if (query.isBlank()) {

            equipment

        } else {

            equipment.filter {

                it.name.contains(
                    query,
                    ignoreCase = true
                ) ||
                        it.id.contains(
                            query,
                            ignoreCase = true
                        ) ||
                        it.category.contains(
                            query,
                            ignoreCase = true
                        ) ||
                        it.laboratory.contains(
                            query,
                            ignoreCase = true
                        )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
    ) {

        Text(
            text = "Equipos de laboratorio",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Consulta disponibilidad y solicita equipos para tus prácticas.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                top = 4.dp,
                bottom = 12.dp
            )
        )

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text("Buscar equipo")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
        )

        Text(
            text = "${filteredEquipment.size} equipo(s) encontrado(s)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                vertical = 12.dp
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(
                items = filteredEquipment,
                key = {
                    it.id
                }
            ) { item ->

                EquipmentCard(
                    equipment = item,
                    onClick = {
                        onEquipmentClick(item)
                    }
                )
            }
        }
    }
}