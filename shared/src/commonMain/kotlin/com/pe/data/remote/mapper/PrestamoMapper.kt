package com.pe.data.remote.mapper

import com.pe.data.remote.dto.DetallePrestamoDto
import com.pe.data.remote.dto.PrestamoDto
import com.pe.domain.model.DetallePrestamo
import com.pe.domain.model.EstadoItem
import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.Prestamo

fun PrestamoDto.toDomain(): Prestamo {
    return Prestamo(
        idPrestamo = idPrestamo,
        idSolicitud = idSolicitud,
        nombreEstudiante = nombreEstudiante ?: "Estudiante desconocido",
        codigoEstudiante = codigoEstudiante.orEmpty(),
        nombreAmbiente = nombreAmbiente ?: "Sin ambiente",
        idEncargadoAprobacion = idEncargadoAprobacion,
        nombreEncargadoAprobacion = nombreEncargadoAprobacion ?: "Pendiente",
        idEncargadoDevolucion = idEncargadoDevolucion,
        nombreEncargadoDevolucion = nombreEncargadoDevolucion,
        fechaInicioUso = fechaInicioUso.orEmpty(),
        fechaFinUso = fechaFinUso.orEmpty(),
        comentarioAprobacion = comentarioAprobacion,
        estado = EstadoPrestamo.desdeTexto(estadoPrestamo),
        detalles = detalles.map { it.toDomain() }
    )
}

fun DetallePrestamoDto.toDomain(): DetallePrestamo {
    return DetallePrestamo(
        idDetalle = idDetalle,
        idEquipo = idEquipo,
        codigoQrEquipo = codigoQrEquipo.orEmpty(),
        nombreEquipo = nombreEquipo ?: "Equipo sin nombre",
        estadoItem = EstadoItem.desdeTexto(estadoItem),
        estadoFisicoAlDevolver = estadoFisicoAlDevolver,
        comentarioDevolucion = comentarioDevolucion
    )

}
