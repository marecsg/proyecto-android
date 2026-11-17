package com.pe.presentation

sealed class Screen {

    object Splash : Screen()

    object Login : Screen()

    object Home : Screen()

    // EQUIPOS
    object DetalleEquipo : Screen()

    // SOLICITUDES
    object CrearSolicitud : Screen()
    object SeleccionEquipos : Screen()
    object DetalleSolicitud : Screen()

    // PRÉSTAMOS
    object DetallePrestamo : Screen()

    // PERFIL
    object ProfileUpdate : Screen()
}
