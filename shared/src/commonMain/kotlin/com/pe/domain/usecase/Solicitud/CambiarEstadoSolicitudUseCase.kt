package com.pe.domain.usecase.Solicitud

import com.pe.domain.model.EstadoSolicitud
import com.pe.domain.model.Solicitud
import com.pe.domain.repository.SolicitudRepository

class CambiarEstadoSolicitudUseCase(
    private val repository: SolicitudRepository
) {
    suspend operator fun invoke(
        idSolicitud: Int,
        nuevoEstado: EstadoSolicitud,
        comentarioRespuesta: String? = null
    ): Result<Solicitud> {

        if (idSolicitud <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "El ID de la solicitud no es válido"
                )
            )
        }

        val comentarioLimpio = comentarioRespuesta
            ?.trim()
            ?.takeIf { it.isNotEmpty() }


        return repository.cambiarEstado(
            idSolicitud = idSolicitud,
            nuevoEstado = nuevoEstado,
            comentarioRespuesta = comentarioLimpio

        )
    }
}
