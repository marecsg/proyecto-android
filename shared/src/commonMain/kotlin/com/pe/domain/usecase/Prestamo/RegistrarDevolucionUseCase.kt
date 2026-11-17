
package com.pe.domain.usecase.Prestamo

import com.pe.domain.model.EstadoItem
import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.Prestamo
import com.pe.domain.repository.PrestamoRepository
import com.pe.domain.repository.PrestamoRepository.DevolucionItem

class RegistrarDevolucionUseCase(
    private val repository: PrestamoRepository
) {

    suspend operator fun invoke(
        prestamo: Prestamo,
        estadoCierre: EstadoPrestamo,
        itemsEvaluados: List<DevolucionItem>
    ): Result<Prestamo> {

        // Solo se pueden devolver préstamos EN_CURSO
        if (prestamo.estado != EstadoPrestamo.EN_CURSO) {
            return Result.failure(
                IllegalStateException(
                    "Solo se puede registrar la devolución " +
                            "de un préstamo en curso."
                )
            )
        }

        // La devolución finaliza el préstamo
        if (estadoCierre != EstadoPrestamo.FINALIZADO) {
            return Result.failure(
                IllegalArgumentException(
                    "La devolución debe finalizar el préstamo."
                )
            )
        }

        // Debe existir al menos un equipo
        if (prestamo.detalles.isEmpty()) {
            return Result.failure(
                IllegalStateException(
                    "El préstamo no tiene equipos asociados."
                )
            )
        }

        val idsEsperados = prestamo.detalles
            .map { it.idDetalle }
            .toSet()

        val idsRecibidos = itemsEvaluados
            .map { it.idDetalle }

        // No permitir equipos duplicados
        if (idsRecibidos.size != idsRecibidos.toSet().size) {
            return Result.failure(
                IllegalArgumentException(
                    "No puedes evaluar dos veces el mismo equipo."
                )
            )
        }

        // TODOS los equipos deben tener una evaluación
        if (
            itemsEvaluados.size != prestamo.detalles.size ||
            idsRecibidos.toSet() != idsEsperados
        ) {
            return Result.failure(
                IllegalArgumentException(
                    "Debes evaluar todos los equipos " +
                            "antes de confirmar la devolución."
                )
            )
        }

        // Validar la condición física de cada equipo devuelto
        for (item in itemsEvaluados) {

            if (
                item.estadoItem == EstadoItem.DEVUELTO &&
                item.estadoFisicoAlDevolver == null
            ) {
                return Result.failure(
                    IllegalArgumentException(
                        "Debes indicar el estado físico " +
                                "de cada equipo devuelto."
                    )
                )
            }
        }

        // Preparar los datos sin modificar las decisiones
        // del encargado.
        val detallesValidados = itemsEvaluados.map { item ->
            item.copy(
                estadoFisicoAlDevolver =
                    if (item.estadoItem == EstadoItem.DEVUELTO) {
                        item.estadoFisicoAlDevolver
                    } else {
                        null
                    },

                comentarioDevolucion = item.comentarioDevolucion
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
            )
        }

        // Enviar la devolución completa al backend
        return repository.registrarDevolucion(
            idPrestamo = prestamo.idPrestamo,
            estadoPrestamo = EstadoPrestamo.FINALIZADO,
            detallesDevolucion = detallesValidados
        )
    }
}
