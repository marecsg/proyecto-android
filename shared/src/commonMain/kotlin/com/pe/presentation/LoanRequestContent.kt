package com.pe.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pe.domain.model.Equipment
import com.pe.domain.model.LoanDraft
import kotlinx.datetime.LocalDate

private enum class FechaCampo { RECOGIDA, DEVOLUCION }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanRequestContent(
    equipment: Equipment,
    requesterName: String,
    isSubmitting: Boolean = false,
    submitError: String? = null,
    onBack: () -> Unit,
    onConfirm: (LoanDraft) -> Unit
) {
    var course by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var returnDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var startPeriod by remember { mutableStateOf("AM") }
    var endPeriod by remember { mutableStateOf("PM") }
    var reason by remember { mutableStateOf("") }
    var pickingDate by remember { mutableStateOf<FechaCampo?>(null) }

    pickingDate?.let { campo ->
        val current = if (campo == FechaCampo.RECOGIDA) date else returnDate
        // Si aún no eligió devolución, el calendario abre en la fecha de recogida.
        val initial = (parseFecha(current) ?: parseFecha(date))
            ?.toEpochDays()?.toLong()?.times(86_400_000L)
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(
            onDismissRequest = { pickingDate = null },
            confirmButton = {
                TextButton(
                    enabled = pickerState.selectedDateMillis != null,
                    onClick = {
                        pickerState.selectedDateMillis?.let { ms ->
                            val selected = LocalDate.fromEpochDays((ms / 86_400_000L).toInt())
                            val formatted = fechaPantalla(selected)
                            if (campo == FechaCampo.RECOGIDA) date = formatted
                            else returnDate = formatted
                        }
                        pickingDate = null
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { pickingDate = null }) { Text("Cancelar") } }
        ) {
            Column {
                Text(
                    if (campo == FechaCampo.RECOGIDA) "Selecciona la fecha de recogida" else "Selecciona la fecha de devolución",
                    modifier = Modifier.padding(start = 24.dp, top = 18.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                DatePicker(state = pickerState)
            }
        }
    }

    val pickupDay = parseFecha(date)
    val returnDay = parseFecha(returnDate)
    val pickupMinutes = timeMinutes12(startTime, startPeriod)
    val returnMinutes = timeMinutes12(endTime, endPeriod)
    val startMoment = if (pickupDay != null && pickupMinutes != null)
        pickupDay.toEpochDays().toLong() * 1440L + pickupMinutes else null
    val endMoment = if (returnDay != null && returnMinutes != null)
        returnDay.toEpochDays().toLong() * 1440L + returnMinutes else null
    val invalidOrder = startMoment != null && endMoment != null && endMoment <= startMoment
    val formIsValid = course.isNotBlank() && teacher.isNotBlank() && reason.isNotBlank() &&
        startMoment != null && endMoment != null && !invalidOrder

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitud de préstamo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Completa los datos de uso",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(equipment.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${equipment.id} • ${equipment.laboratory}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("Solicitante: $requesterName", style = MaterialTheme.typography.bodyMedium)
                }
            }
            OutlinedTextField(
                value = course, onValueChange = { course = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Curso") }, singleLine = true
            )
            OutlinedTextField(
                value = teacher, onValueChange = { teacher = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Docente responsable") }, singleLine = true
            )

            DateInputField(
                value = date,
                label = "Fecha de recogida",
                onClick = { pickingDate = FechaCampo.RECOGIDA }
            )
            HourInput(
                label = "Hora de recogida", value = startTime,
                onValueChange = { startTime = it }, period = startPeriod,
                onPeriodChange = { startPeriod = it }
            )
            DateInputField(
                value = returnDate,
                label = "Fecha de devolución",
                onClick = { pickingDate = FechaCampo.DEVOLUCION }
            )
            HourInput(
                label = "Hora de devolución", value = endTime,
                onValueChange = { endTime = it }, period = endPeriod,
                onPeriodChange = { endPeriod = it }
            )

            Text(
                "Ejemplo: recogida 10:00 AM y devolución 05:00 PM. También puedes devolver al día siguiente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (invalidOrder) {
                Text(
                    "La fecha y hora de devolución deben ser posteriores a las de recogida.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            OutlinedTextField(
                value = reason, onValueChange = { reason = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Motivo / observación") }, minLines = 3
            )
            if (submitError != null) Text(submitError, color = MaterialTheme.colorScheme.error)
            Button(
                onClick = {
                    onConfirm(
                        LoanDraft(
                            course = course.trim(),
                            teacher = teacher.trim(),
                            date = date,
                            returnDate = returnDate,
                            startTime = to24Hours(startTime, startPeriod),
                            endTime = to24Hours(endTime, endPeriod),
                            reason = reason.trim()
                        )
                    )
                },
                enabled = formIsValid && !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text(if (isSubmitting) "Enviando al servidor..." else "Confirmar solicitud")
            }
        }
    }
}

@Composable
private fun DateInputField(value: String, label: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value, onValueChange = {}, modifier = Modifier.fillMaxWidth(),
            label = { Text(label) }, placeholder = { Text("Seleccionar fecha") },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            readOnly = true, singleLine = true
        )
        Box(modifier = Modifier.matchParentSize().clickable(onClick = onClick))
    }
}

@Composable
private fun HourInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    period: String,
    onPeriodChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }.take(4)
                onValueChange(if (digits.length > 2) digits.take(2) + ":" + digits.drop(2) else digits)
            },
            modifier = Modifier.weight(1f),
            label = { Text(label) },
            placeholder = { Text("hh:mm") },
            supportingText = { Text("01:00 a 12:59") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = value.length == 5 && timeMinutes12(value, period) == null
        )
        FilterChip(
            selected = period == "AM", onClick = { onPeriodChange("AM") },
            label = { Text("AM") }
        )
        FilterChip(
            selected = period == "PM", onClick = { onPeriodChange("PM") },
            label = { Text("PM") }
        )
    }
}

private fun parseFecha(value: String): LocalDate? {
    val p = value.split("/")
    if (p.size != 3) return null
    return runCatching { LocalDate(p[2].toInt(), p[1].toInt(), p[0].toInt()) }.getOrNull()
}

private fun fechaPantalla(date: LocalDate): String =
    "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year}"

private fun timeMinutes12(text: String, period: String): Int? {
    if (!Regex("[0-9]{2}:[0-9]{2}").matches(text)) return null
    val hour = text.take(2).toIntOrNull() ?: return null
    val minutes = text.takeLast(2).toIntOrNull() ?: return null
    if (hour !in 1..12 || minutes !in 0..59) return null
    val hour24 = hour % 12 + if (period == "PM") 12 else 0
    return hour24 * 60 + minutes
}

private fun to24Hours(value: String, period: String): String {
    val mins = requireNotNull(timeMinutes12(value, period)) { "Hora inválida" }
    return "${(mins / 60).toString().padStart(2, '0')}:${(mins % 60).toString().padStart(2, '0')}"
}
