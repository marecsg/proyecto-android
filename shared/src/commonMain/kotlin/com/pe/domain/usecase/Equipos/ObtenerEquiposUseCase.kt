package com.pe.domain.usecase.Equipos

import com.pe.domain.model.Equipo
import com.pe.domain.repository.EquipoRepository

class ObtenerEquiposUseCase(private val repository: EquipoRepository) {
    suspend operator fun invoke(): Result<List<Equipo>> {
        return repository.obtenerEquipos()
    }
}
