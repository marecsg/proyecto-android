package com.pe.data.remote.mapper

import com.pe.data.remote.dto.EquipoDto
import com.pe.domain.model.Equipment
import com.pe.domain.model.EquipmentStatus

fun EquipoDto.toDomain(): Equipment {
    val status = when {
        estadoFisico != "OPERATIVO" -> EquipmentStatus.MAINTENANCE
        disponibilidad == "DISPONIBLE" -> EquipmentStatus.AVAILABLE
        else -> EquipmentStatus.LOANED
    }
    return Equipment(
        id = codigoQr.orEmpty(),
        name = nombre.orEmpty(),
        category = "Equipo de laboratorio",
        laboratory = ambiente?.nombre.orEmpty(),
        status = status,
        description = descripcion.orEmpty(),
        remoteId = idEquipo
    )
}
