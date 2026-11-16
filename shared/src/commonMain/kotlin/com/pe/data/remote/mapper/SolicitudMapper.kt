
package com.pe.data.remote.mapper

import com.pe.data.remote.dto.SolicitudDto
import com.pe.data.remote.dto.SolicitudDetalleDto
import com.pe.domain.model.Solicitud
import com.pe.domain.model.DetalleSolicitud
import com.pe.domain.model.EstadoSolicitud

fun SolicitudDto.toDomain(): Solicitud {
    return Solicitud(
        idSolicitud = idSolicitud,
        idEstudiante = idEstudiante,
        nombreEstudiante = nombreEstudiante,
        codigoEstudiante = codigoEstudiante.orEmpty(),
        idAmbiente = idAmbiente,
        nombreAmbiente = nombreAmbiente,
        motivoSolicitud = motivoSolicitud,
        fechaInicioReserva = fechaInicioReserva,
        fechaFinReserva = fechaFinReserva,
        fechaSolicitud = fechaSolicitud,
        fechaRespuesta = fechaRespuesta,
        comentarioRespuesta = comentarioRespuesta,
        estado = EstadoSolicitud.desdeTexto(estadoSolicitud),
        detalles = detalles.map { it.toDomain() }
    )
}

fun SolicitudDetalleDto.toDomain(): DetalleSolicitud {
    return DetalleSolicitud(
        idEquipo = idEquipo,
        codigoQrEquipo = codigoQrEquipo,
        nombreEquipo = nombreEquipo
    )
}
