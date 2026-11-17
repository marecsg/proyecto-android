
package com.pe.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pe.domain.model.DisponibilidadEquipo
import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.EstadoSolicitud
import com.pe.presentation.theme.*

enum class TonoEstado {
    SUCCESS,
    WARNING,
    ERROR,
    INFO,
    NEUTRAL
}

@Composable
fun BadgeEstado(
    label: String,
    tone: TonoEstado,
    modifier: Modifier = Modifier
) {
    val (colorTexto, colorFondo) = when (tone) {
        TonoEstado.SUCCESS -> SuccessText to SuccessContainer
        TonoEstado.WARNING -> WarningText to WarningContainer
        TonoEstado.ERROR -> ErrorText to ErrorContainer
        TonoEstado.INFO -> PrimaryDark to PrimaryContainer
        TonoEstado.NEUTRAL -> TextMuted to SurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        color = colorFondo
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            color = colorTexto,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

fun DisponibilidadEquipo.toTono(): TonoEstado = when (this) {
    DisponibilidadEquipo.DISPONIBLE -> TonoEstado.SUCCESS
    DisponibilidadEquipo.PRESTADO -> TonoEstado.WARNING
    DisponibilidadEquipo.NO_DISPONIBLE -> TonoEstado.ERROR
}

fun EstadoSolicitud.toTono(): TonoEstado = when (this) {
    EstadoSolicitud.PENDIENTE -> TonoEstado.WARNING
    EstadoSolicitud.APROBADA -> TonoEstado.SUCCESS
    EstadoSolicitud.RECHAZADA,
    EstadoSolicitud.CANCELADA -> TonoEstado.ERROR
}

fun EstadoPrestamo.toTono(): TonoEstado = when (this) {
    EstadoPrestamo.PENDIENTE,
    EstadoPrestamo.VENCIDO -> TonoEstado.WARNING
    EstadoPrestamo.EN_CURSO -> TonoEstado.INFO
    EstadoPrestamo.FINALIZADO -> TonoEstado.SUCCESS
    EstadoPrestamo.CANCELADO -> TonoEstado.ERROR
}
