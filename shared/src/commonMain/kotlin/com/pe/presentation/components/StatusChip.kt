package com.pe.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.pe.domain.model.IncidentStatus
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pe.domain.model.EquipmentStatus
import com.pe.domain.model.LoanStatus

@Composable
fun EquipmentStatusChip(
    status: EquipmentStatus
) {

    val containerColor = when (status) {
        EquipmentStatus.AVAILABLE -> Color(0xFFE8F5E9)
        EquipmentStatus.LOANED -> Color(0xFFFFF3E0)
        EquipmentStatus.MAINTENANCE -> Color(0xFFFFEBEE)
    }

    val contentColor = when (status) {
        EquipmentStatus.AVAILABLE -> Color(0xFF2E7D32)
        EquipmentStatus.LOANED -> Color(0xFFEF6C00)
        EquipmentStatus.MAINTENANCE -> Color(0xFFC62828)
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
        )
    }
}

@Composable
fun LoanStatusChip(
    status: LoanStatus
) {

    val containerColor = when (status) {
        LoanStatus.PENDING -> Color(0xFFFFF8E1)
        LoanStatus.APPROVED -> Color(0xFFE8F5E9)
        LoanStatus.IN_USE -> Color(0xFFE3F2FD)
        LoanStatus.RETURNED -> Color(0xFFF1F3F4)
        LoanStatus.REJECTED -> Color(0xFFFFEBEE)
        LoanStatus.CANCELLED -> Color(0xFFF1F3F4)
    }

    val contentColor = when (status) {
        LoanStatus.PENDING -> Color(0xFFF9A825)
        LoanStatus.APPROVED -> Color(0xFF2E7D32)
        LoanStatus.IN_USE -> Color(0xFF1565C0)
        LoanStatus.RETURNED -> Color(0xFF546E7A)
        LoanStatus.REJECTED -> Color(0xFFC62828)
        LoanStatus.CANCELLED -> Color(0xFF616161)
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
        )
    }
}

@Composable
fun IncidentStatusChip(
    status: IncidentStatus
) {

    val containerColor = when (status) {
        IncidentStatus.REPORTED -> Color(0xFFFFF3E0)
        IncidentStatus.IN_REVIEW -> Color(0xFFE3F2FD)
        IncidentStatus.RESOLVED -> Color(0xFFE8F5E9)
    }

    val contentColor = when (status) {
        IncidentStatus.REPORTED -> Color(0xFFEF6C00)
        IncidentStatus.IN_REVIEW -> Color(0xFF1565C0)
        IncidentStatus.RESOLVED -> Color(0xFF2E7D32)
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
        )
    }
}