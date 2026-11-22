package com.pe.domain.usecase.Solicitud

import com.pe.domain.model.Solicitud
import com.pe.domain.repository.SolicitudRepository

class CrearSolicitudUseCase(
    private val repository: SolicitudRepository
) {
    suspend operator fun invoke(
        idEstudiante: Int,
        idAmbiente: Int,
        motivoSolicitud: String,
        fechaInicioReserva: String,
        fechaFinReserva: String,
        idsEquipos: List<Int>,
        aceptoTerminos: Boolean
    ): Result<Solicitud> {

        if (idsEquipos.isEmpty()) {
            return Result.failure(
                IllegalArgumentException(
                    "Debes seleccionar al menos un equipo"
                )
            )
        }

        if (motivoSolicitud.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "Debes ingresar el motivo de la solicitud"
                )
            )
        }

        if (!aceptoTerminos) {
            return Result.failure(
                IllegalArgumentException(
                    "Debes aceptar los términos y condiciones"
                )
            )
        }

        return repository.crearSolicitud(
            idEstudiante = idEstudiante,
            idAmbiente = idAmbiente,
            motivoSolicitud = motivoSolicitud.trim(),
            fechaInicioReserva = fechaInicioReserva,
            fechaFinReserva = fechaFinReserva,
            idsEquipos = idsEquipos,
            aceptoTerminos = aceptoTerminos
        )
    }
}
