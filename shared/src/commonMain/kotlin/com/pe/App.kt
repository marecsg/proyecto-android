package com.pe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pe.di.appModule
import com.pe.domain.model.*
import com.pe.presentation.Screen
import com.pe.presentation.screens.auth.*
import com.pe.presentation.screens.equipos.*
import com.pe.presentation.screens.prestamos.*
import com.pe.presentation.screens.solicitudes.*
import com.pe.presentation.theme.AppTheme
import com.pe.presentation.viewmodel.*
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        val authViewModel = koinViewModel<AuthViewModel>()
        val equipoViewModel = koinViewModel<EquipoViewModel>()
        val solicitudViewModel = koinViewModel<SolicitudViewModel>()
        val prestamoViewModel = koinViewModel<PrestamoViewModel>()

        val auth by authViewModel.state.collectAsState()
        val equipoState by equipoViewModel.uiState.collectAsState()
        val solicitudState by solicitudViewModel.uiState.collectAsState()
        val prestamoState by prestamoViewModel.uiState.collectAsState()
        val usuario = (auth as? AuthState.Success)?.usuario

        var pantalla by remember { mutableStateOf<Screen>(Screen.Splash) }
        var pestana by remember { mutableIntStateOf(0) }
        var idEquipoDetalle by remember { mutableStateOf<Int?>(null) }
        var idSolicitudDetalle by remember { mutableStateOf<Int?>(null) }
        var idPrestamoDetalle by remember { mutableStateOf<Int?>(null) }
        var idsSeleccionados by remember { mutableStateOf<Set<Int>>(emptySet()) }
        var aviso by remember { mutableStateOf<String?>(null) }

        fun abrirFormulario(ids: Set<Int> = emptySet()) {
            idsSeleccionados = ids
            solicitudViewModel.clearError()
            solicitudViewModel.clearSuccess()
            pantalla = Screen.CrearSolicitud
        }

        fun cargarListas() {
            val u = usuario ?: return
            equipoViewModel.load()
            if (u.esEncargado) {
                solicitudViewModel.loadManager(u)
                prestamoViewModel.loadManager(u)
            } else {
                solicitudViewModel.loadStudent(u)
                prestamoViewModel.loadStudent(u)
            }
        }

        LaunchedEffect(pantalla, usuario?.idUsuario) {
            if (pantalla == Screen.Home && usuario != null) cargarListas()
        }

        LaunchedEffect(solicitudState.successMessage) {
            if (pantalla == Screen.CrearSolicitud && solicitudState.successMessage != null) {
                idsSeleccionados = emptySet()
                pestana = 1
                pantalla = Screen.Home
                solicitudViewModel.clearSuccess()
            }
        }

        AppTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                when (pantalla) {
                    Screen.Splash -> SplashContent(
                        onTimeout = {
                            pantalla = if (usuario == null) Screen.Login else Screen.Home
                        }
                    )

                    Screen.Login -> WelcomeContent(
                        viewModel = authViewModel,
                        onLoginSuccess = {
                            pestana = 0
                            pantalla = Screen.Home
                        }
                    )

                    Screen.Home -> {
                        if (usuario == null) {
                            WelcomeContent(
                                viewModel = authViewModel,
                                onLoginSuccess = { pantalla = Screen.Home }
                            )
                        } else {
                            HomeContent(
                                selectedTab = pestana,
                                onTabSelected = { pestana = it },
                                onLogout = {
                                    authViewModel.resetState()
                                    equipoViewModel.clear()
                                    solicitudViewModel.clear()
                                    prestamoViewModel.clear()
                                    idsSeleccionados = emptySet()
                                    pantalla = Screen.Login
                                },
                                onEditProfile = { pantalla = Screen.ProfileUpdate },
                                onScanQr = {
                                    aviso = "El lector QR se conectará al integrar el escáner."
                                }
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    when (pestana) {
                                        0 -> EquipoContent(
                                            equipos = equipoState.equipos,
                                            ambientes = equipoState.equipos
                                                .distinctBy { it.idAmbiente }
                                                .map { OpcionAmbiente(it.idAmbiente, it.nombreAmbiente) },
                                            onClick = { id ->
                                                idEquipoDetalle = id
                                                pantalla = Screen.DetalleEquipo
                                            }
                                        )

                                        1 -> {
                                            PrestamosContent(
                                                solicitudes = solicitudState.solicitudes,
                                                prestamos = prestamoState.prestamos,
                                                esEncargado = usuario.esEncargado,
                                                onVerDetalleSolicitud = { id ->
                                                    idSolicitudDetalle = id
                                                    solicitudViewModel.cargarDetalle(id)
                                                    pantalla = Screen.DetalleSolicitud
                                                },
                                                onVerDetallePrestamo = { id ->
                                                    idPrestamoDetalle = id
                                                    prestamoViewModel.cargarDetalle(id)
                                                    pantalla = Screen.DetallePrestamo
                                                },
                                                onIniciarPrestamo = { id ->
                                                    prestamoViewModel.cambiarEstado(id, EstadoPrestamo.EN_CURSO)
                                                },
                                                onRegistrarDevolucion = {
                                                    aviso = "La pantalla de devolución será integrada por Marec."
                                                },
                                                cargandoSolicitudes = solicitudState.loading,
                                                cargandoPrestamos = prestamoState.loading,
                                                errorSolicitudes = solicitudState.errorMessage,
                                                errorPrestamos = prestamoState.errorMessage
                                            )
                                            if (!usuario.esEncargado) {
                                                Button(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .padding(16.dp),
                                                    onClick = { abrirFormulario() }
                                                ) { Text("+ Nueva solicitud") }
                                            }
                                        }

                                        2, 3 -> Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(if (pestana == 2) "Incidencias en desarrollo" else "Historial en desarrollo")
                                        }
                                    }
                                    if (aviso != null) {
                                        Snackbar(
                                            modifier = Modifier.align(Alignment.BottomCenter),
                                            action = { TextButton(onClick = { aviso = null }) { Text("Cerrar") } }
                                        ) { Text(aviso.orEmpty()) }
                                    }
                                }
                            }
                        }
                    }
                    Screen.DetalleEquipo -> {
                        val equipo = equipoState.equipos.firstOrNull { it.idEquipo == idEquipoDetalle }
                        if (equipo == null) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                TextButton(onClick = { pantalla = Screen.Home }) { Text("Volver a equipos") }
                            }
                        } else {
                            DetalleEquipoContent(
                                equipo = equipo,
                                onBack = { pantalla = Screen.Home },
                                onRequestLoan = {
                                    if (usuario?.esEncargado == true) {
                                        aviso = "Las solicitudes se crean desde una cuenta de estudiante."
                                    } else {
                                        abrirFormulario(setOf(equipo.idEquipo))
                                    }
                                },
                                onReportIncident = { aviso = "Incidencias se integrará próximamente." }
                            )
                        }
                    }

                    Screen.CrearSolicitud -> {
                        val seleccionados = equipoState.equipos.filter { it.idEquipo in idsSeleccionados }
                        CrearSolicitudContent(
                            equiposSeleccionados = seleccionados,
                            procesando = solicitudState.processing,
                            error = solicitudState.errorMessage,
                            onVolver = { pantalla = Screen.Home },
                            onElegirDeLista = { pantalla = Screen.SeleccionEquipos },
                            onEscanearQr = { aviso = "El lector QR aún no está conectado." },
                            onQuitarEquipo = { id -> idsSeleccionados = idsSeleccionados - id },
                            onEnviarSolicitud = { inicio, fin, motivo ->
                                val u = usuario
                                val idAmbiente = seleccionados.firstOrNull()?.idAmbiente
                                if (u != null && idAmbiente != null && seleccionados.isNotEmpty()) {
                                    solicitudViewModel.crear(
                                        usuario = u,
                                        idAmbiente = idAmbiente,
                                        motivo = motivo,
                                        fechaInicio = inicio,
                                        fechaFin = fin,
                                        idsEquipos = seleccionados.map { it.idEquipo }
                                    )
                                }
                            }
                        )
                    }
                    Screen.SeleccionEquipos -> SeleccionEquiposContent(
                        equipos = equipoState.equipos,
                        idsSeleccionados = idsSeleccionados,
                        onSeleccionarEquipo = { id ->
                            idsSeleccionados = if (id in idsSeleccionados) idsSeleccionados - id
                            else idsSeleccionados + id
                        },
                        onContinuar = { pantalla = Screen.CrearSolicitud },
                        onVolver = { pantalla = Screen.CrearSolicitud }
                    )

                    Screen.DetalleSolicitud -> {
                        val solicitud = solicitudState.solicitudSeleccionada
                            ?.takeIf { it.idSolicitud == idSolicitudDetalle }
                            ?: solicitudState.solicitudes.firstOrNull { it.idSolicitud == idSolicitudDetalle }
                        if (solicitud == null) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            DetalleSolicitudContent(
                                solicitud = solicitud,
                                numero = solicitud.idSolicitud,
                                esEncargado = usuario?.esEncargado == true,
                                onBack = { pantalla = Screen.Home },
                                onCancelar = { id ->
                                    solicitudViewModel.cambiarEstado(id, EstadoSolicitud.CANCELADA)
                                },
                                onAprobar = { id, comentario ->
                                    val u = usuario
                                    if (u != null) {
                                        prestamoViewModel.aprobar(u, id, comentario) { prestamo ->
                                            prestamoViewModel.cargarDetalle(prestamo.idPrestamo)
                                            idPrestamoDetalle = prestamo.idPrestamo
                                            solicitudViewModel.cargarDetalle(id)
                                            pestana = 1
                                            pantalla = Screen.DetallePrestamo
                                        }
                                    }
                                },
                                onRechazar = { id, comentario ->
                                    solicitudViewModel.cambiarEstado(id, EstadoSolicitud.RECHAZADA, comentario)
                                },
                                onVerPrestamo = { idSolicitud ->
                                    val p = prestamoState.prestamos.firstOrNull { it.idSolicitud == idSolicitud }
                                    if (p == null) aviso = "Actualiza la lista de préstamos y vuelve a intentar."
                                    else {
                                        idPrestamoDetalle = p.idPrestamo
                                        prestamoViewModel.cargarDetalle(p.idPrestamo)
                                        pantalla = Screen.DetallePrestamo
                                    }
                                },
                                onVolverASolicitar = {
                                    idsSeleccionados = solicitud.detalles.map { it.idEquipo }.toSet()
                                    abrirFormulario(idsSeleccionados)
                                },
                                procesando = solicitudState.processing || prestamoState.processing
                            )
                        }
                    }
                    Screen.DetallePrestamo -> {
                        val prestamo = prestamoState.prestamoSeleccionado
                            ?.takeIf { it.idPrestamo == idPrestamoDetalle }
                            ?: prestamoState.prestamos.firstOrNull { it.idPrestamo == idPrestamoDetalle }
                        if (prestamo == null) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            DetallePrestamoContent(
                                prestamo = prestamo,
                                esEncargado = usuario?.esEncargado == true,
                                solicitud = solicitudState.solicitudes.firstOrNull {
                                    it.idSolicitud == prestamo.idSolicitud
                                },
                                onVolver = { pantalla = Screen.Home },
                                onCancelarPrestamo = {
                                    prestamoViewModel.cambiarEstado(it, EstadoPrestamo.CANCELADO)
                                },
                                onIniciarPrestamo = {
                                    prestamoViewModel.cambiarEstado(it, EstadoPrestamo.EN_CURSO)
                                },
                                onRegistrarDevolucion = {
                                    aviso = "Pantalla de devolución pendiente de Marec."
                                },
                                procesando = prestamoState.processing,
                                error = prestamoState.errorMessage
                            )
                        }
                    }

                    Screen.ProfileUpdate -> ProfileUpdateContent(
                        onBack = { pantalla = Screen.Home },
                        onSave = { pantalla = Screen.Home }
                    )

                    else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        TextButton(onClick = { pantalla = Screen.Home }) {
                            Text("Volver al inicio")
                        }
                    }
                }
            }
        }
    }
}