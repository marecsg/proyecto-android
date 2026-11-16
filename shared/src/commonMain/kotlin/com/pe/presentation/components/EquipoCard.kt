
package com.pe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pe.domain.model.Equipo
import com.pe.presentation.theme.*

@Composable

fun EquipoCard(
    equipo: Equipo,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    seleccionada: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(equipo.idEquipo)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada)
                PrimarySoft else SurfaceWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (seleccionada) Primary else Border
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = equipo.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark
                    )

                    Text(
                        text = equipo.codigoQr,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                BadgeEstado(
                    label = equipo.disponibilidad.etiqueta,
                    tone = equipo.disponibilidad.toTono()
                )
            }

            Text(
                text = equipo.nombreAmbiente,
                style = MaterialTheme.typography.bodyMedium,
                color = TextBody
            )

            Text(
                text = equipo.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 2
            )

            Text(
                text = "Ver detalle",
                style = MaterialTheme.typography.labelMedium,
                color = Primary
            )
        }
    }
}
