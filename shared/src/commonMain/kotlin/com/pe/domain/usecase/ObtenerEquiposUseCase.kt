package com.pe.domain.usecase

import com.pe.domain.model.Equipment
import com.pe.domain.repository.EquipoRepository

class ObtenerEquiposUseCase(private val repository: EquipoRepository) {
    suspend operator fun invoke(): Result<List<Equipment>> {
        return repository.obtenerEquipos()
    }
}
