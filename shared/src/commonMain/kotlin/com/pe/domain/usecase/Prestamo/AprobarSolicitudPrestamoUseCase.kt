package com.pe.domain.usecase.Prestamo

import com.pe.domain.model.Prestamo
import com.pe.domain.repository.PrestamoRepository

class AprobarSolicitudPrestamoUseCase(private val repository: PrestamoRepository) {

    suspend operator fun invoke(idSolicitud: Int, idEncargadoAprobacion: Int,
        comentarioAprobacion: String? = null

    ): Result<Prestamo> {

        val comentarioLimpio = comentarioAprobacion?.trim()?.takeIf { it.isNotEmpty() }

        return repository.crearDesdeSolicitud(
            idSolicitud = idSolicitud,
            idEncargadoAprobacion = idEncargadoAprobacion,
            comentarioAprobacion = comentarioLimpio
        )
    }
}