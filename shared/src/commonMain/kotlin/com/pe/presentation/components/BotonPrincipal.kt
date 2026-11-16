
package com.pe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pe.presentation.theme.Primary
import com.pe.presentation.theme.BorderStrong
import com.pe.presentation.theme.TextBody
import com.pe.presentation.theme.TextLight

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    secundario: Boolean = false
) {
    if (secundario) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = habilitado,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BorderStrong),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextBody
            )
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.titleMedium
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = habilitado,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = TextLight
            )
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
