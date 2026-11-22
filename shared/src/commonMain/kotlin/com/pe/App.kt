package com.pe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pe.data.notification.LocalNotificationPlatform
import com.pe.data.notification.UnsupportedLocalNotificationPlatform
import com.pe.presentation.NotificationSettingsContent
import com.pe.presentation.viewmodel.NotificationSettingsViewModel

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

import com.pe.presentation.screens.incidencias.*
import com.pe.presentation.screens.historial.HistoryContent
import org.koin.dsl.module

enum class OrigenEscaneoQr {
    CONSULTAR_EQUIPO,
    AGREGAR_A_SOLICITUD
}

data class LecturaQr(
    val codigo: String,
    val origen: OrigenEscaneoQr
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    onAbrirEscanerQr: (OrigenEscaneoQr) -> Unit = {},
    lecturaQr: LecturaQr? = null,
    onQrConsumido: () -> Unit = {},
    notificationPlatform: LocalNotificationPlatform = UnsupportedLocalNotificationPlatform
) {
    val notificationModule = remember(notificationPlatform){
        module { single<LocalNotificationPlatform>{notificationPlatform} }
    }

    KoinApplication(application = { modules(appModule,notificationModule) }) {
        val authViewModel = koinViewModel<AuthViewModel>()
        val equipoViewModel = koinViewModel<EquipoViewModel>()
        val solicitudViewModel = koinViewModel<SolicitudViewModel>()
        val prestamoViewModel = koinViewModel<PrestamoViewModel>()
        val incidenciaViewModel = koinViewModel<IncidenciaViewModel>()
        val historialViewModel = koinViewModel<HistorialViewModel>()

        val notificationSettingsViewModel = koinViewModel<NotificationSettingsViewModel>()
        val auth by authViewModel.state.collectAsState()
        val equipoState by equipoViewModel.uiState.collectAsState()
        val solicitudState by solicitudViewModel.uiState.collectAsState()
        val prestamoState by prestamoViewModel.uiState.collectAsState()
        val incidenciaState by incidenciaViewModel.uiState.collectAsState()
        val historialState by historialViewModel.uiState.collectAsState()
        val usuario = (auth as? AuthState.Success)?.usuario

        var pantalla by remember { mutableStateOf<Screen>(Screen.Splash) }
        var pestana by remember { mutableIntStateOf(0) }
        var idEquipoDetalle by remember { mutableStateOf<Int?>(null) }
        var idSolicitudDetalle by remember { mutableStateOf<Int?>(null) }
        var idPrestamoDetalle by remember { mutableStateOf<Int?>(null) }
        var idIncidenciaDetalle by remember {
            mutableStateOf<Int?>(null)
        }
        var idsSeleccionados by remember { mutableStateOf<Set<Int>>(emptySet()) }
        var aviso by remember { mutableStateOf<String?>(null) }
        var mostrarFichaQr by remember {
            mutableStateOf(false)
        }

        var origenQrActual by remember {
            mutableStateOf(OrigenEscaneoQr.CONSULTAR_EQUIPO)
        }

        var equiposEscaneados by remember {
            mutableStateOf<List<Equipo>>(emptyList())
        }

        LaunchedEffect(lecturaQr) {
            if (lecturaQr != null) {
                origenQrActual = lecturaQr.origen
                mostrarFichaQr = true

                equipoViewModel.limpiarSeleccion()
                equipoViewModel.buscarPorQr(lecturaQr.codigo)

                onQrConsumido()
            }
        }

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

        fun abrirDevolucion(idPrestamo: Int) {
            if (usuario?.esEncargado != true) return

            idPrestamoDetalle = idPrestamo

            prestamoViewModel.clearError()
            prestamoViewModel.clearSuccess()
            prestamoViewModel.cargarDetalle(idPrestamo)

            pantalla = Screen.RegistrarDevolucion
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

        LaunchedEffect(pantalla, pestana, usuario?.idUsuario) {
            val u = usuario

            if (
                pantalla == Screen.Home &&
                pestana == 2 &&
                u != null
            ) {
                if (u.esEncargado) {
                    // Incidencias del laboratorio
                    incidenciaViewModel.loadManager(u)
                } else {
                    // Incidencias reportadas por el estudiante
                    incidenciaViewModel.loadStudent(u)
                }
            }
        }

        // CARGAR HISTORIAL AL ABRIR LA PESTAÑA 3
        LaunchedEffect(
            pantalla,
            pestana,
            usuario?.idUsuario,
            usuario?.rol
        ) {
            val u = usuario

            if (
                pantalla == Screen.Home &&
                pestana == 3 &&
                u != null
            ) {
                historialViewModel.cargarHistorial(u)
            }
        }

        LaunchedEffect(
            prestamoState.successMessage,
            pantalla
        ) {
            if (
                pantalla == Screen.RegistrarDevolucion &&
                prestamoState.successMessage ==
                "Devolución registrada con éxito"
            ) {
                prestamoViewModel.clearSuccess()

                idPrestamoDetalle?.let { id ->
                    prestamoViewModel.cargarDetalle(id)
                }

                pantalla = Screen.DetallePrestamo
            }
        }

        LaunchedEffect(
            incidenciaState.successMessage,
            pantalla
        ) {
            if (
                pantalla == Screen.ReportarIncidencia &&
                incidenciaState.successMessage ==
                "Incidencia registrada correctamente"
            ) {
                incidenciaState.ultimaCreada?.let { creada ->
                    idIncidenciaDetalle = creada.idTicket
                    incidenciaViewModel.clearSuccess()
                    pantalla = Screen.DetalleIncidencia
                }
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

                    Screen.NotificationSetting -> {
                        NotificationSettingsContent(
                            viewModel = notificationSettingsViewModel,
                            onBack = {
                                pantalla = Screen.Home
                            }
                        )
                    }

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
                                    historialViewModel.limpiar()
                                    incidenciaViewModel.clear()
                                    idIncidenciaDetalle = null
                                    idsSeleccionados = emptySet()
                                    pantalla = Screen.Login
                                },
                                        onEditProfile = { pantalla = Screen.ProfileUpdate },

                                onSettingsClick = {pantalla= Screen.NotificationSetting},

                                        onScanQr = {
                                    onAbrirEscanerQr(
                                        OrigenEscaneoQr.CONSULTAR_EQUIPO
                                    )
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
                                                onRegistrarDevolucion = { idPrestamo ->
                                                    abrirDevolucion(idPrestamo)
                                                },
                                                cargandoSolicitudes = solicitudState.loading,
                                                cargandoPrestamos = prestamoState.loading,
                                                errorSolicitudes = solicitudState.errorMessage,
                                                errorPrestamos = prestamoState.errorMessage
                                            )
                                            if (!usuario.esEncargado) {
                                                Button(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomStart)
                                                        .padding(16.dp),
                                                    onClick = { abrirFormulario() }
                                                ) { Text("+ Nueva solicitud") }
                                            }
                                        }



                                        2 -> IncidenciasContent(
                                            incidencias = incidenciaState.incidencias,
                                            esEncargado = usuario.esEncargado,

                                            // El backend ya permite listar
                                            // las incidencias del estudiante
                                            listadoEstudianteDisponible = !usuario.esEncargado,

                                            cargando = incidenciaState.loading,
                                            error = incidenciaState.errorMessage,

                                            onVerDetalle = { id ->
                                                idIncidenciaDetalle = id

                                                incidenciaViewModel.clearError()
                                                incidenciaViewModel.cargarDetalle(id)

                                                pantalla = Screen.DetalleIncidencia
                                            },

                                            onReportarIncidencia = {
                                                incidenciaViewModel.clearError()
                                                incidenciaViewModel.clearSuccess()

                                                pantalla = Screen.ReportarIncidencia
                                            }
                                        )

                                        3 -> HistoryContent(
                                            estado = historialState,
                                            esEncargado = usuario.esEncargado,
                                            onRecargar = {
                                                historialViewModel.cargarHistorial(usuario)
                                            }
                                        )

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
                        val equiposDisponibles = (
                                equiposEscaneados + equipoState.equipos
                                ).distinctBy { it.idEquipo }

                        val seleccionados = equiposDisponibles.filter {
                            it.idEquipo in idsSeleccionados
                        }
                        CrearSolicitudContent(
                            equiposSeleccionados = seleccionados,
                            procesando = solicitudState.processing,
                            error = solicitudState.errorMessage,
                            onVolver = { pantalla = Screen.Home },
                            onElegirDeLista = { pantalla = Screen.SeleccionEquipos },
                            onEscanearQr = {
                                onAbrirEscanerQr(
                                    OrigenEscaneoQr.AGREGAR_A_SOLICITUD
                                )
                            },
                            onQuitarEquipo = { id -> idsSeleccionados = idsSeleccionados - id },
                            onEnviarSolicitud = { inicio, fin, motivo, aceptoTerminos ->
                                val u = usuario
                                val idAmbiente = seleccionados.firstOrNull()?.idAmbiente

                                if (
                                    u != null &&
                                    idAmbiente != null &&
                                    seleccionados.isNotEmpty() &&
                                    aceptoTerminos
                                ) {
                                    solicitudViewModel.crear(
                                        usuario = u,
                                        idAmbiente = idAmbiente,
                                        motivo = motivo,
                                        fechaInicio = inicio,
                                        fechaFin = fin,
                                        idsEquipos = seleccionados.map { it.idEquipo },
                                        aceptoTerminos = aceptoTerminos
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
                                onRegistrarDevolucion = { idPrestamo ->
                                    abrirDevolucion(idPrestamo)
                                },
                                procesando = prestamoState.processing,
                                error = prestamoState.errorMessage
                            )
                        }
                    }

                    Screen.RegistrarDevolucion -> {

                        val prestamo = prestamoState.prestamoSeleccionado
                            ?.takeIf { it.idPrestamo == idPrestamoDetalle }
                            ?: prestamoState.prestamos.firstOrNull {
                                it.idPrestamo == idPrestamoDetalle
                            }

                        if (prestamo == null) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (prestamoState.errorMessage != null) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            prestamoState.errorMessage.orEmpty()
                                        )

                                        TextButton(
                                            onClick = {
                                                pantalla = Screen.DetallePrestamo
                                            }
                                        ) {
                                            Text("Volver")
                                        }
                                    }
                                } else {
                                    CircularProgressIndicator()
                                }
                            }
                        } else {
                            RegistrarDevolucionContent(
                                prestamo = prestamo,

                                procesando = prestamoState.processing,

                                error = prestamoState.errorMessage,

                                onVolver = {
                                    pantalla = Screen.DetallePrestamo
                                },

                                onConfirmar = { itemsEvaluados ->
                                    val u = usuario

                                    if (u != null && u.esEncargado) {
                                        prestamoViewModel.confirmarDevolucion(
                                            usuario = u,
                                            prestamo = prestamo,
                                            estadoCierre = EstadoPrestamo.FINALIZADO,
                                            itemsEvaluados = itemsEvaluados
                                        )
                                    }
                                }
                            )
                        }
                    }


                    Screen.ReportarIncidencia -> {
                        val u = usuario

                        if (u != null && !u.esEncargado) {
                            ReportarIncidenciaContent(
                                ambientes = equipoState.equipos
                                    .distinctBy { it.idAmbiente }
                                    .map {
                                        OpcionAmbiente(
                                            it.idAmbiente,
                                            it.nombreAmbiente
                                        )
                                    },

                                equipos = equipoState.equipos,
                                procesando = incidenciaState.processing,
                                error = incidenciaState.errorMessage,

                                onVolver = {
                                    pestana = 2
                                    pantalla = Screen.Home
                                },

                                onRegistrar = {
                                        idAmbiente,
                                        idEquipo,
                                        tipoIncidencia,
                                        descripcion ->

                                    incidenciaViewModel.registrar(
                                        usuario = u,
                                        idAmbiente = idAmbiente,
                                        idEquipo = idEquipo,
                                        tipoIncidencia = tipoIncidencia,
                                        descripcion = descripcion
                                    )
                                }
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(
                                    onClick = { pantalla = Screen.Home }
                                ) {
                                    Text("Formulario disponible para estudiantes. Volver")
                                }
                            }
                        }
                    }

                    Screen.DetalleIncidencia -> {

                        val incidencia = incidenciaState.incidenciaSeleccionada
                            ?.takeIf {
                                it.idTicket == idIncidenciaDetalle
                            }
                            ?: incidenciaState.incidencias.firstOrNull {
                                it.idTicket == idIncidenciaDetalle
                            }

                        if (incidencia == null) {

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (incidenciaState.loading) {
                                    CircularProgressIndicator()
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            incidenciaState.errorMessage
                                                ?: "No se encontró la incidencia"
                                        )

                                        TextButton(
                                            onClick = {
                                                pestana = 2
                                                pantalla = Screen.Home
                                            }
                                        ) {
                                            Text("Volver")
                                        }
                                    }
                                }
                            }

                        } else {

                            DetalleIncidenciaContent(
                                incidencia = incidencia,
                                esEncargado = usuario?.esEncargado == true,
                                procesando = incidenciaState.processing,
                                error = incidenciaState.errorMessage,

                                onVolver = {
                                    pestana = 2
                                    pantalla = Screen.Home
                                },

                                onActualizarEstado = {
                                        idTicket,
                                        nuevoEstado,
                                        respuesta ->

                                    val u = usuario

                                    if (u != null && u.esEncargado) {
                                        incidenciaViewModel.cambiarEstado(
                                            usuario = u,
                                            idTicket = idTicket,
                                            nuevoEstado = nuevoEstado,
                                            respuestaEncargado = respuesta
                                        )
                                    }
                                }
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
        if (mostrarFichaQr) {
            ModalBottomSheet(
                onDismissRequest = {
                    mostrarFichaQr = false
                    equipoViewModel.limpiarSeleccion()
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when {
                        equipoState.loading -> {
                            CircularProgressIndicator()
                            Text("Buscando equipo...")
                        }

                        equipoState.errorMessage != null -> {
                            Text(
                                "No se encontró el equipo",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                equipoState.errorMessage.orEmpty()
                            )
                        }

                        equipoState.equipoSeleccionado != null -> {
                            val equipo = equipoState.equipoSeleccionado!!

                            Text(
                                "Equipo escaneado",
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                equipo.nombre,
                                style = MaterialTheme.typography.titleLarge
                            )

                            HorizontalDivider()

                            Text("Código: ${equipo.codigoQr}")
                            Text("Ubicación: ${equipo.nombreAmbiente}")
                            Text("Estado: ${equipo.disponibilidad.etiqueta}")

                            val equiposActuales =
                                equiposEscaneados + equipoState.equipos

                            val mismoAmbiente = idsSeleccionados.all { id ->
                                equiposActuales
                                    .firstOrNull { it.idEquipo == id }
                                    ?.idAmbiente == equipo.idAmbiente
                            }

                            if (
                                origenQrActual ==
                                OrigenEscaneoQr.AGREGAR_A_SOLICITUD
                            ) {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = equipo.estaDisponible &&
                                            mismoAmbiente,
                                    onClick = {
                                        equiposEscaneados =
                                            (equiposEscaneados + equipo)
                                                .distinctBy { it.idEquipo }

                                        idsSeleccionados =
                                            idsSeleccionados + equipo.idEquipo

                                        mostrarFichaQr = false
                                        equipoViewModel.limpiarSeleccion()
                                    }
                                ) {
                                    Text("+ Agregar a la solicitud")
                                }

                                if (!equipo.estaDisponible) {
                                    Text("Este equipo no está disponible.")
                                } else if (!mismoAmbiente) {
                                    Text(
                                        "Los equipos deben pertenecer al mismo ambiente."
                                    )
                                }
                            }
                        }

                        else -> {
                            Text("Consultando información del equipo...")
                        }
                    }

                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            mostrarFichaQr = false
                            equipoViewModel.limpiarSeleccion()
                        }
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}