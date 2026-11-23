package com.pe.data.remote.mapper

import com.pe.data.remote.dto.HistorialDto
import com.pe.domain.model.Historial

fun HistorialDto.toDomain(): Historial {
    return Historial(
        idHistorial = idHistorial,
        idUsuarioActor = idUsuarioActor,
        nombreActor = nombreActor,
        idUsuarioAfectado = idUsuarioAfectado,
        nombreAfectado = nombreAfectado,
        tipoAccion = tipoAccion,
        tipoEntidad = tipoEntidad,
        codigoEntidad = codigoEntidad,
        idEntidad = idEntidad,
        descripcion = descripcion,
        fechaAccion = fechaAccion
    )
}