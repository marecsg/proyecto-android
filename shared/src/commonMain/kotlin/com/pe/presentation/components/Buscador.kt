
package com.pe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pe.presentation.theme.*

@Composable
fun Buscador(
    texto: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar...",
    onAbrirFiltros: (() -> Unit)? = null,
    filtrosActivos: Int = 0
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = texto,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark
            )
        )

        if (onAbrirFiltros != null) {
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
                    onClick = onAbrirFiltros,
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        1.dp,
                        if (filtrosActivos > 0) Primary else Border
                    )
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
    }
}
