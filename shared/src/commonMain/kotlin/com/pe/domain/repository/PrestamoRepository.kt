package com.pe.domain.repository

import com.pe.domain.model.EstadoFisicoEquipo
import com.pe.domain.model.EstadoItem
import com.pe.domain.model.EstadoPrestamo
import com.pe.domain.model.Prestamo
import com.pe.domain.model.Usuario

interface PrestamoRepository {

    suspend fun obtenerPrestamoPorId(idPrestamo: Int): Result<Prestamo>
    suspend fun obtenerPorEstudiante(idEstudiante: Int): Result<List<Prestamo>>
    suspend fun obtenerPorAmbiente(idAmbiente: Int): Result<List<Prestamo>>

    suspend fun crearDesdeSolicitud(
        idSolicitud: Int,
        idEncargadoAprobacion: Int,
        comentarioAprobacion:
        String? = null
    ): Result<Prestamo>

    suspend fun cambiarEstado(idPrestamo: Int, nuevoEstado: EstadoPrestamo): Result<Prestamo>

    suspend fun registrarDevolucion(
        idPrestamo: Int,
        idEncargadoDevolucion: Int,
        estadoPrestamo: EstadoPrestamo,
        detallesDevolucion: List<DevolucionItem>
    ): Result<Prestamo>

    data class DevolucionItem(
        val idDetalle: Int,
        val estadoItem: EstadoItem,
        val estadoFisicoAlDevolver: EstadoFisicoEquipo,
        val comentarioDevolucion: String? = null
    )
}
