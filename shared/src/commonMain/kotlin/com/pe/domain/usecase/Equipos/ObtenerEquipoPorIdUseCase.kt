package com.pe.domain.usecase.Equipos

import com.pe.domain.model.Equipo
import com.pe.domain.repository.EquipoRepository

class ObtenerEquipoPorIdUseCase(private val repository: EquipoRepository) {
    suspend operator fun invoke(idEquipo: Int): Result<Equipo> {
        return repository.obtenerEquipoPorId(idEquipo)
    }
}