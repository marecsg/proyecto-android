package com.pe.presentation.screens.prestamos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pe.presentation.components.LoanStatusChip
import com.pe.domain.model.Loan
import com.pe.domain.model.LoanStatus
import com.pe.presentation.format.horaLegible

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailContent(
    loan: Loan,
    onBack: () -> Unit,
    onCancelLoan: () -> Unit
) {

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Detalle del préstamo")
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = loan.equipment.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = loan.id,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LoanStatusChip(
                    status = loan.status
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    LoanDetailRow(
                        label = "Equipo",
                        value = loan.equipment.name
                    )

                    LoanDetailRow(
                        label = "Código del equipo",
                        value = loan.equipment.id
                    )

                    LoanDetailRow(
                        label = "Laboratorio",
                        value = loan.equipment.laboratory
                    )

                    LoanDetailRow(
                        label = "Solicitante",
                        value = loan.requester
                    )

                    LoanDetailRow(
                        label = "Curso",
                        value = loan.course
                    )

                    LoanDetailRow(
                        label = "Docente",
                        value = loan.teacher
                    )

                    LoanDetailRow(
                        label = "Fecha de recogida",
                        value = loan.date
                    )

                    LoanDetailRow(
                        label = "Hora de recogida",
                        value = horaLegible(loan.startTime)
                    )

                    LoanDetailRow(
                        label = "Fecha de devolución",
                        value = loan.returnDate
                    )

                    LoanDetailRow(
                        label = "Hora de devolución",
                        value = horaLegible(loan.endTime)
                    )
                }
            }

            Column {

                Text(
                    text = "Motivo / observación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = loan.reason,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            if (loan.status == LoanStatus.PENDING) {

                Text(
                    text = "Solicitud pendiente. La cancelación desde Android aún no está habilitada en esta versión.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LoanDetailRow(
    label: String,
    value: String
) {

    Column {

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}