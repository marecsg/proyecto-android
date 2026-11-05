package com.pe

import com.pe.data.remote.dto.*
import com.pe.data.remote.mapper.toDomain
import com.pe.domain.model.LabSession
import com.pe.domain.model.LoanStatus
import com.pe.presentation.format.horaLegible
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapperAndDomainTest {
    @Test
    fun convierteEstadosYEquipoDelBackend() {
        val equipo = EquipoDto(
            idEquipo = 2,
            codigoQr = "EQ-QR-002",
            nombre = "Switch Cisco",
            disponibilidad = "DISPONIBLE",
            estadoFisico = "OPERATIVO",
            ambiente = AmbienteDto("Laboratorio de Redes")
        ).toDomain()
        assertEquals("EQ-QR-002", equipo.id)
        assertEquals("Laboratorio de Redes", equipo.laboratory)
        assertEquals(2, equipo.remoteId)
        assertEquals("Disponible", equipo.status.label)
    }

    @Test
    fun conviertePrestamosDeVariosDias() {
        val dto = PrestamoDto(
            idSolicitud = 4,
            estudiante = UsuarioDto(1, "Williams", "Santisteban"),
            equipo = EquipoDto(idEquipo = 2, codigoQr = "EQ-QR-002", nombre = "Switch"),
            motivoSolicitud = "Curso: Redes; Docente: Profesor; Motivo: Práctica de examen",
            fechaInicioUso = "2026-11-05T10:00:00",
            fechaFinUso = "2026-11-11T17:00:00",
            estadoSolicitud = "RECHAZADA"
        )
        val prestamo = dto.toDomain()
        assertEquals("PRE-4", prestamo.id)
        assertEquals(LoanStatus.REJECTED, prestamo.status)
        assertEquals("05/11/2026", prestamo.date)
        assertEquals("11/11/2026", prestamo.returnDate)
        assertEquals("17:00", prestamo.endTime)
        assertEquals("Redes", prestamo.course)
        assertEquals("Profesor", prestamo.teacher)
        assertEquals("Williams Santisteban", prestamo.requester)
    }

    @Test
    fun seConservaRolDelUsuario() {
        val encargado = LabSession(3, "Carlos Docente", "docente@upeu.edu.pe", "Encargado", "token")
        val estudiante = encargado.copy(rol = "Estudiante")
        assertTrue(encargado.esEncargado)
        assertFalse(estudiante.esEncargado)
        assertEquals("5:00 PM", horaLegible("17:00"))
    }
}
