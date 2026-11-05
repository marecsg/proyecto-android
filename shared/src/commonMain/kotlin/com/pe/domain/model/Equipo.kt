package com.pe.domain.model

enum class EquipmentStatus(val label: String) {
    AVAILABLE("Disponible"),
    LOANED("Prestado"),
    MAINTENANCE("Mantenimiento")
}

data class Equipment(
    val id: String,
    val name: String,
    val category: String,
    val laboratory: String,
    val status: EquipmentStatus,
    val description: String,
    val remoteId: Int = 0
)
