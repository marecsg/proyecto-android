package com.pe.presentation.screens.prestamos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pe.domain.model.Loan
import com.pe.domain.model.LoanStatus
import com.pe.presentation.format.rangoLegible
import com.pe.presentation.theme.BackgroundClean
import com.pe.presentation.theme.RoyalBlue
import com.pe.presentation.theme.TextDark
import com.pe.presentation.theme.TextMuted

// Paleta de LabManager: se reutilizan los colores ya existentes del proyecto.
private val ManagerGreen = Color(0xFF16803B)
private val ManagerGreenBg = Color(0xFFE6F4EA)
private val ManagerOrange = Color(0xFF9E6300)
private val ManagerOrangeBg = Color(0xFFFFF2D9)
private val ManagerRed = Color(0xFFB42318)
private val ManagerRedBg = Color(0xFFFDE8E7)
private val ManagerBlueBg = Color(0xFFE8F0FC)
private val ManagerStroke = Color(0xFFE5EAF2)

/**
 * Panel del encargado.
 * Firma compatible con App.kt y el backend actual: no requiere otros cambios.
 */
@Composable
fun ManagerLoansContent(
    nombre: String,
    loans: List<Loan>,
    loading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onRespond: (Loan, Boolean) -> Unit
) {
    val pendientes = loans.filter { it.status == LoanStatus.PENDING }
    val respondidas = loans.filter { it.status != LoanStatus.PENDING }
    val aprobadas = loans.count { it.status == LoanStatus.APPROVED }
    val rechazadas = loans.count { it.status == LoanStatus.REJECTED }
    val devueltas = loans.count { it.status == LoanStatus.RETURNED }

    var tab by remember { mutableStateOf(0) }
    var confirmacion by remember { mutableStateOf<Pair<Loan, Boolean>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundClean)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Mantiene la cabecera debajo de la barra de estado (sin chocar con la cámara).
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RoyalBlue)
                .padding(start = 20.dp, end = 10.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Gestión de préstamos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    "Encargado · $nombre",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onRefresh, enabled = !loading) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Actualizar solicitudes",
                    tint = Color.White
                )
            }
            TextButton(
                onClick = onLogout,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Salir", fontWeight = FontWeight.SemiBold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 14.dp)
        ) {
            Text(
                "Solicitudes de equipos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                "Gestiona las solicitudes de préstamo de los estudiantes",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(Modifier.height(16.dp))

            // Resumen con datos reales de las solicitudes recibidas.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                ManagerSummaryCard(
                    "Pendientes", pendientes.size, ManagerOrange, ManagerOrangeBg,
                    Modifier.weight(1f)
                )
                ManagerSummaryCard(
                    "Aprobadas", aprobadas, ManagerGreen, ManagerGreenBg,
                    Modifier.weight(1f)
                )
                ManagerSummaryCard(
                    "Rechazadas", rechazadas, ManagerRed, ManagerRedBg,
                    Modifier.weight(1f)
                )
                ManagerSummaryCard(
                    "Devueltas", devueltas, RoyalBlue, ManagerBlueBg,
                    Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ManagerTab(
                    label = "Pendientes (${pendientes.size})",
                    selected = tab == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { tab = 0 }
                )
                ManagerTab(
                    label = "Respondidas (${respondidas.size})",
                    selected = tab == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { tab = 1 }
                )
            }
        }

        if (loading) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Text("Actualizando solicitudes...", style = MaterialTheme.typography.bodySmall)
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            Surface(
                color = ManagerRedBg,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                Text(
                    errorMessage,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = ManagerRed
                )
            }
        }

        val visibles = if (tab == 0) pendientes else respondidas
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (visibles.isEmpty()) {
                item {
                    ManagerEmptyState(isPending = tab == 0, onRefresh = onRefresh)
                }
            }
            items(visibles, key = { it.id }) { loan ->
                ManagerLoanCard(
                    loan = loan,
                    isPending = tab == 0,
                    enabled = !loading,
                    onApprove = { confirmacion = loan to true },
                    onReject = { confirmacion = loan to false }
                )
            }
        }
    }

    confirmacion?.let { (loan, aprobar) ->
        AlertDialog(
            onDismissRequest = { confirmacion = null },
            title = { Text(if (aprobar) "¿Aprobar solicitud?" else "¿Rechazar solicitud?") },
            text = {
                val resultado = if (aprobar) {
                    "El estudiante verá el préstamo como aprobado."
                } else {
                    "El estudiante verá la solicitud como rechazada."
                }
                Text(
                    "${loan.id} · ${loan.equipment.name}\n" +
                        "Estudiante: ${loan.requester}\n\n" + resultado
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        confirmacion = null
                        onRespond(loan, aprobar)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (aprobar) RoyalBlue else ManagerRed
                    )
                ) {
                    Text(if (aprobar) "Sí, aprobar" else "Sí, rechazar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmacion = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ManagerSummaryCard(
    label: String,
    count: Int,
    foreground: Color,
    background: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = background,
        shape = RoundedCornerShape(13.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                "$count",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = foreground
            )
            Text(
                label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = foreground,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ManagerTab(
    label: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier.height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            Text(label, maxLines = 1, style = MaterialTheme.typography.labelMedium)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(44.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 5.dp),
            border = BorderStroke(1.dp, ManagerStroke)
        ) {
            Text(label, maxLines = 1, style = MaterialTheme.typography.labelMedium, color = TextDark)
        }
    }
}

@Composable
private fun ManagerEmptyState(isPending: Boolean, onRefresh: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, ManagerStroke)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(color = ManagerGreenBg, shape = RoundedCornerShape(50)) {
                Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ManagerGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Text(
                if (isPending) "¡Todo al día!" else "Sin solicitudes respondidas",
                color = TextDark,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                if (isPending) "No tienes solicitudes pendientes por revisar."
                else "Las solicitudes aprobadas o rechazadas aparecerán aquí.",
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall
            )
            if (isPending) {
                OutlinedButton(onClick = onRefresh, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Actualizar")
                }
            }
        }
    }
}

@Composable
private fun ManagerLoanCard(
    loan: Loan,
    isPending: Boolean,
    enabled: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    // Las respondidas se muestran compactas; pulsar «Ver detalles» revela el contenido.
    var expanded by remember(loan.id) { mutableStateOf(false) }
    val details = remember(loan.reason) { parseManagerLoanReason(loan.reason) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ManagerStroke),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = ManagerBlueBg, shape = RoundedCornerShape(12.dp)) {
                    Box(modifier = Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = RoyalBlue)
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        loan.equipment.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${loan.id}  ·  ${loan.equipment.id}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Spacer(Modifier.width(5.dp))
                ManagerStatePill(loan.status)
            }

            ManagerInfoRow(
                icon = { Icon(Icons.Default.PersonOutline, null, tint = TextMuted, modifier = Modifier.size(17.dp)) },
                text = loan.requester
            )
            ManagerInfoRow(
                icon = { Icon(Icons.Default.Schedule, null, tint = TextMuted, modifier = Modifier.size(17.dp)) },
                text = loan.rangoLegible()
            )

            if (isPending || expanded) {
                Surface(color = BackgroundClean, shape = RoundedCornerShape(12.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "DETALLES DE LA SOLICITUD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        if (details.curso != null) ManagerDetailLine("Curso", details.curso)
                        if (details.docente != null) ManagerDetailLine("Docente", details.docente)
                        ManagerDetailLine("Motivo", details.motivo)
                    }
                }
            }

            if (isPending) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        enabled = enabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                    ) {
                        Text("Aprobar", fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = onReject,
                        enabled = enabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ManagerRed)
                    ) {
                        Text("Rechazar", color = ManagerRed, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (expanded) "Ocultar detalles" else "Ver detalles",
                        color = RoyalBlue,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = RoyalBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ManagerInfoRow(icon: @Composable () -> Unit, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icon()
        Text(text, style = MaterialTheme.typography.bodySmall, color = TextDark)
    }
}

@Composable
private fun ManagerDetailLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextDark)
    }
}

private data class ManagerReasonParts(
    val curso: String?,
    val docente: String?,
    val motivo: String
)

/**
 * Soporta tanto el formulario nuevo:
 * "Curso: ...; Docente: ...; Motivo: ..."
 * como las solicitudes antiguas de Postman, que solo tienen un motivo libre.
 */
private fun parseManagerLoanReason(raw: String): ManagerReasonParts {
    if (raw.isBlank()) return ManagerReasonParts(null, null, "Sin motivo registrado")
    val parts = raw.split(";").map { it.trim() }.filter { it.isNotBlank() }
    val containsLabels = parts.any {
        it.startsWith("Curso:", ignoreCase = true) ||
            it.startsWith("Docente:", ignoreCase = true) ||
            it.startsWith("Motivo:", ignoreCase = true)
    }
    if (!containsLabels) return ManagerReasonParts(null, null, raw.trim())

    var course: String? = null
    var teacher: String? = null
    val motives = mutableListOf<String>()
    parts.forEach { part ->
        when {
            part.startsWith("Curso:", ignoreCase = true) -> {
                course = part.substringAfter(":").trim().takeIf { it.isNotBlank() }
            }
            part.startsWith("Docente:", ignoreCase = true) -> {
                teacher = part.substringAfter(":").trim().takeIf { it.isNotBlank() }
            }
            part.startsWith("Motivo:", ignoreCase = true) -> {
                motives.add(part.substringAfter(":").trim())
            }
            else -> motives.add(part)
        }
    }
    return ManagerReasonParts(
        curso = course,
        docente = teacher,
        motivo = motives.joinToString("; ").ifBlank { "Sin motivo registrado" }
    )
}

@Composable
private fun ManagerStatePill(status: LoanStatus) {
    val (foreground, background) = when (status) {
        LoanStatus.PENDING -> ManagerOrange to ManagerOrangeBg
        LoanStatus.APPROVED -> ManagerGreen to ManagerGreenBg
        LoanStatus.REJECTED, LoanStatus.CANCELLED -> ManagerRed to ManagerRedBg
        LoanStatus.IN_USE -> RoyalBlue to ManagerBlueBg
        LoanStatus.RETURNED -> TextMuted to BackgroundClean
    }
    Surface(color = background, shape = RoundedCornerShape(50)) {
        Text(
            status.label,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = foreground,
            maxLines = 1
        )
    }
}
