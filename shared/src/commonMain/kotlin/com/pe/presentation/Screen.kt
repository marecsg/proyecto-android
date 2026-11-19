
package com.pe.presentation

sealed class Screen {

    object Splash : Screen()

    object Login : Screen()

    object Home : Screen()

    // EQUIPOS
    object DetalleEquipo : Screen()
    object EscanearQr : Screen()

    // SOLICITUDES
    object CrearSolicitud : Screen()
    object SeleccionEquipos : Screen()
    object DetalleSolicitud : Screen()

    // PRÉSTAMOS
    object DetallePrestamo : Screen()
    object RegistrarDevolucion : Screen()

    // INCIDENCIAS - NUEVAS RUTAS
    object ReportarIncidencia : Screen()
    object DetalleIncidencia : Screen()

    // PERFIL
    object ProfileUpdate : Screen()
}
