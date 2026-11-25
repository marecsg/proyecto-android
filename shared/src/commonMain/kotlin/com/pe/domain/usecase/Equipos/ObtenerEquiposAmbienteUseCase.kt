package com.pe.domain.usecase.Equipos

import com.pe.domain.model.Equipo
import com.pe.domain.repository.EquipoRepository

class ObtenerEquiposAmbienteUseCase(
    private val repository: EquipoRepository
) {
    suspend operator fun invoke(
        idAmbiente: Int
    ): Result<List<Equipo>> {

        return repository.obtenerEquiposPorAmbiente(idAmbiente)
    }
}