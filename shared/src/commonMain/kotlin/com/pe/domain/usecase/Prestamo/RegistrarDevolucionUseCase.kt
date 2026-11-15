package com.pe.domain.usecase.Prestamo

import com.pe.domain.model.DetallePrestamo
import com.pe.domain.model.EstadoFisicoEquipo
import com.pe.domain.model.EstadoItem
import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.Prestamo
import com.pe.domain.repository.PrestamoRepository
import com.pe.domain.repository.PrestamoRepository.DevolucionItem

class RegistrarDevolucionUseCase(private val repository: PrestamoRepository) {
    suspend operator fun invoke(
        prestamo: Prestamo,
        idEncargadoDevolucion: Int,
        estadoCierre: EstadoPrestamo,
        itemsEvaluados: List<DevolucionItem>
    ): Result<Prestamo> {
        if (prestamo.detalles.isEmpty()) {
            return Result.failure(IllegalStateException("El préstamo no tiene equipos asociados."))
        }

        val evaluadosPorDetalle = itemsEvaluados.associateBy { it.idDetalle }

        val bloqueCompleto = prestamo.detalles.map { detalle: DetallePrestamo ->

            val evaluado = evaluadosPorDetalle[detalle.idDetalle]

            if (evaluado != null) {
                evaluado.copy(
                    comentarioDevolucion = evaluado.comentarioDevolucion?.trim()?.takeIf { it.isNotEmpty() }
                )
            } else {
                DevolucionItem(
                    idDetalle = detalle.idDetalle,
                    estadoItem = EstadoItem.NO_DEVUELTO,
                    estadoFisicoAlDevolver = EstadoFisicoEquipo.DEFECTUOSO,
                    comentarioDevolucion = "No entregado al cierre del préstamo"
                )
            }
        }

        return repository.registrarDevolucion(
            idPrestamo = prestamo.idPrestamo,
            idEncargadoDevolucion = idEncargadoDevolucion,
            estadoPrestamo = estadoCierre,
            detallesDevolucion = bloqueCompleto
        )
    }
}