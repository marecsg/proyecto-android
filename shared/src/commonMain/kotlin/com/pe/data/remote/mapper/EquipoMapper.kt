package com.pe.data.remote.mapper

import com.pe.data.remote.dto.EquipoDto
import com.pe.domain.model.DisponibilidadEquipo
import com.pe.domain.model.Equipo
import com.pe.domain.model.EstadoFisicoEquipo

fun EquipoDto.toDomain(): Equipo {
    return Equipo(
        idEquipo = idEquipo,
        codigoQr = codigoQr.orEmpty(),
        nombre = nombre ?: "Equipo sin nombre",
        descripcion = descripcion ?: "Sin descripción",
        idAmbiente = idAmbiente ?: 0,
        nombreAmbiente = nombreAmbiente ?: "Sin ambiente asignado",
        estadoFisico = EstadoFisicoEquipo.desdeTexto(estadoFisico),
        disponibilidad = DisponibilidadEquipo.desdeTexto(disponibilidad)
    )
}