package com.pe.data.remote.mapper

import com.pe.data.remote.dto.TicketIncidenciaDto
import com.pe.domain.model.Incidencia
import com.pe.domain.model.EstadoTicket
import com.pe.domain.model.TipoIncidencia

fun TicketIncidenciaDto.toDomain(): Incidencia {
    return Incidencia(
        idTicket = idTicket,
        idUsuarioReporta = idUsuarioReporta,
        nombreUsuarioReporta = nombreUsuarioReporta,
        idAmbiente = idAmbiente,
        nombreAmbiente = nombreAmbiente,
        idEquipo = idEquipo,
        nombreEquipo = nombreEquipo,
        tipoIncidencia = TipoIncidencia.desdeTexto(
            tipoIncidencia
        ),
        descripcion = descripcion,
        estadoTicket = EstadoTicket.desdeTexto(
            estadoTicket
        ),
        fechaReporte = fechaReporte,
        respuestaEncargado = respuestaEncargado,
        fechaResolucion = fechaResolucion,
        codigoCorrelativo = codigoCorrelativo,
        nombreEncargadoAtencion = nombreEncargadoAtencion,
        nombreEncargadoResolucion = nombreEncargadoResolucion
    )
}