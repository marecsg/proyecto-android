package com.pe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pe.di.appModule
import com.pe.domain.model.*
import com.pe.presentation.*
import com.pe.presentation.screens.incidentes.IncidentDetailContent
import com.pe.presentation.screens.incidentes.IncidentReportContent
import com.pe.presentation.screens.auth.HomeContent
import com.pe.presentation.screens.auth.ProfileUpdateContent
import com.pe.presentation.screens.auth.SplashContent
import com.pe.presentation.screens.auth.WelcomeContent
import com.pe.presentation.screens.equipos.EquipmentDetailContent
import com.pe.presentation.screens.prestamos.LoanConfirmationContent
import com.pe.presentation.screens.prestamos.LoanDetailContent
import com.pe.presentation.screens.prestamos.LoanRequestContent
import com.pe.presentation.screens.prestamos.ManagerLoansContent
import com.pe.presentation.theme.AppTheme
import com.pe.presentation.viewmodel.AuthViewModel
import com.pe.presentation.viewmodel.AuthState // <-- Importante el State
import com.pe.presentation.viewmodel.EquipoViewModel
import com.pe.presentation.viewmodel.PrestamoViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        val authViewModel = koinViewModel<AuthViewModel>()
        val equipoViewModel = koinViewModel<EquipoViewModel>()
        val prestamoViewModel = koinViewModel<PrestamoViewModel>()

        val authState by authViewModel.state.collectAsState()
        val equipoState by equipoViewModel.uiState.collectAsState()
        val prestamoState by prestamoViewModel.uiState.collectAsState()

        val session = (authState as? AuthState.Success)?.usuario

        AppTheme {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
            var homeSelectedTab by remember { mutableStateOf(0) }
            var selectedEquipment by remember { mutableStateOf<Equipment?>(null) }
            var selectedLoan by remember { mutableStateOf<Loan?>(null) }
            var selectedIncident by remember { mutableStateOf<Incident?>(null) }
            var incidents by remember { mutableStateOf<List<Incident>>(emptyList()) }
            var historyEntries by remember { mutableStateOf<List<HistoryEntry>>(emptyList()) }
            var refreshVersion by remember { mutableStateOf(0) }
            var localMessage by remember { mutableStateOf<String?>(null) }

            fun logout() {
                authViewModel.resetState()
                equipoViewModel.clear()
                prestamoViewModel.clear()
                selectedEquipment = null
                selectedLoan = null
                localMessage = null
                currentScreen = Screen.Welcome
            }

            LaunchedEffect(currentScreen, session?.idUsuario, refreshVersion) {
                val s = session ?: return@LaunchedEffect
                if (currentScreen != Screen.Home) return@LaunchedEffect

                val esEncargadoRol = s.rol.equals("ENCARGADO", ignoreCase = true)

                if (esEncargadoRol) {
                    prestamoViewModel.loadManager(s)
                } else {
                    equipoViewModel.load()
                    prestamoViewModel.loadStudent(s)
                }
            }

            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                when (currentScreen) {
                    Screen.Splash -> SplashContent(onTimeout = {
                        currentScreen = if (session != null) Screen.Home else Screen.Welcome
                    })

                    Screen.Welcome -> WelcomeContent(
                        viewModel = authViewModel,
                        onLoginSuccess = {
                            homeSelectedTab = 0
                            refreshVersion++
                            currentScreen = Screen.Home
                        }
                    )

                    Screen.Home -> {
                        val s = session
                        if (s == null) {
                            WelcomeContent(
                                viewModel = authViewModel,
                                onLoginSuccess = { currentScreen = Screen.Home }
                            )
                        } else if (s.rol.equals("ENCARGADO", ignoreCase = true)) {
                            ManagerLoansContent(
                                nombre = s.nombres,
                                loans = prestamoState.solicitudesEncargado,
                                loading = prestamoState.loading || prestamoState.processing,
                                errorMessage = prestamoState.errorMessage,
                                onRefresh = { refreshVersion++ },
                                onLogout = { logout() },
                                onRespond = { loan, aceptar ->
                                    prestamoViewModel.responder(s, loan, aceptar)
                                }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize()) {
                                HomeContent(
                                    isLoggedIn = true,
                                    selectedTab = homeSelectedTab,
                                    equipment = equipoState.equipos,
                                    incidents = incidents,
                                    loans = prestamoState.prestamos,
                                    historyEntries = historyEntries,
                                    userName = s.nombres,
                                    userEmail = s.correo,
                                    onTabSelected = { homeSelectedTab = it; refreshVersion++ },
                                    onEquipmentClick = {
                                        selectedEquipment = it; currentScreen =
                                        Screen.EquipmentDetail
                                    },
                                    onLoanClick = {
                                        selectedLoan = it; currentScreen = Screen.LoanDetail
                                    },
                                    onIncidentClick = {
                                        selectedIncident = it; currentScreen = Screen.IncidentDetail
                                    },
                                    onLogout = { logout() },
                                    onEditProfile = { currentScreen = Screen.ProfileUpdate },
                                    onReportIncident = { currentScreen = Screen.IncidentReport },
                                    onScanQr = {
                                        localMessage =
                                            "El escáner QR todavía no está implementado. Selecciona un equipo de la lista."
                                    }
                                )
                                if (equipoState.loading || prestamoState.loading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                                val error = localMessage ?: equipoState.errorMessage ?: prestamoState.errorMessage
                                if (error != null) {
                                    androidx.compose.material3.Snackbar(
                                        modifier = Modifier.align(Alignment.BottomCenter),
                                        action = {
                                            androidx.compose.material3.TextButton(onClick = {
                                                localMessage = null
                                                prestamoViewModel.clearError()
                                            }) { androidx.compose.material3.Text("Cerrar") }
                                        }
                                    ) { androidx.compose.material3.Text(error) }
                                }
                            }
                        }
                    }

                    Screen.EquipmentDetail -> {
                        val eq = selectedEquipment
                        if (eq != null) EquipmentDetailContent(
                            equipment = eq,
                            onBack = { currentScreen = Screen.Home },
                            onRequestLoan = {
                                prestamoViewModel.clearError()
                                currentScreen = Screen.LoanRequest
                            }
                        ) else currentScreen = Screen.Home
                    }

                    Screen.LoanRequest -> {
                        val eq = selectedEquipment
                        val s = session
                        if (eq != null && s != null) LoanRequestContent(
                            equipment = eq,
                            requesterName = s.nombres,
                            isSubmitting = prestamoState.processing,
                            submitError = prestamoState.errorMessage,
                            onBack = {
                                prestamoViewModel.clearError()
                                currentScreen = Screen.EquipmentDetail
                            },
                            onConfirm = { draft ->
                                prestamoViewModel.solicitar(s, eq, draft) {
                                    currentScreen = Screen.LoanConfirmation
                                }
                            }
                        ) else currentScreen = Screen.Home
                    }

                    Screen.LoanConfirmation -> {
                        val loan = prestamoState.ultimoCreado
                        if (loan != null) LoanConfirmationContent(
                            loan = loan,
                            onViewLoans = {
                                homeSelectedTab = 1
                                selectedEquipment = null
                                refreshVersion++
                                currentScreen = Screen.Home
                            },
                            onBackHome = {
                                homeSelectedTab = 0
                                selectedEquipment = null
                                refreshVersion++
                                currentScreen = Screen.Home
                            }
                        ) else currentScreen = Screen.Home
                    }

                    Screen.LoanDetail -> {
                        val loan = selectedLoan
                        if (loan != null) LoanDetailContent(
                            loan = loan,
                            onBack = {
                                homeSelectedTab = 1; refreshVersion++; currentScreen = Screen.Home
                            },
                            onCancelLoan = {
                                localMessage =
                                    "La cancelación requiere un endpoint nuevo en el backend"
                            }
                        ) else currentScreen = Screen.Home
                    }

                    Screen.IncidentReport -> IncidentReportContent(
                        equipmentList = equipoState.equipos,
                        onBack = { currentScreen = Screen.Home; homeSelectedTab = 2 },
                        onConfirm = { draft ->
                            incidents = listOf(
                                Incident(
                                    id = "INC-${1001 + incidents.size}",
                                    equipment = draft.equipment,
                                    reporter = session?.nombres ?: "Estudiante",
                                    type = draft.type,
                                    description = draft.description,
                                    date = draft.date,
                                    status = IncidentStatus.REPORTED
                                )
                            ) + incidents
                            homeSelectedTab = 2
                            currentScreen = Screen.Home
                        }
                    )

                    Screen.IncidentDetail -> {
                        val incident = selectedIncident
                        if (incident != null) IncidentDetailContent(
                            incident = incident,
                            onBack = { homeSelectedTab = 2; currentScreen = Screen.Home }
                        ) else currentScreen = Screen.Home
                    }

                    Screen.ProfileUpdate -> ProfileUpdateContent(
                        onBack = { currentScreen = Screen.Home },
                        onSave = { _ -> currentScreen = Screen.Home }
                    )
                }
            }
        }
    }
}