package com.pe.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import com.pe.domain.model.HistoryEntry
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import com.pe.domain.model.Incident
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pe.domain.model.Equipment
import com.pe.domain.model.Loan
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    isLoggedIn: Boolean,
    userName: String = "Estudiante",
    userEmail: String = "",
    selectedTab: Int,
    equipment: List<Equipment>,
    loans: List<Loan>,
    incidents: List<Incident>,
    historyEntries: List<HistoryEntry>,
    onTabSelected: (Int) -> Unit,
    onEquipmentClick: (Equipment) -> Unit,
    onLoanClick: (Loan) -> Unit,
    onIncidentClick: (Incident) -> Unit,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onReportIncident: () -> Unit,
    onScanQr: () -> Unit
) {

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            MaterialTheme.colorScheme.primary
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    if (isLoggedIn) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Box(
                                contentAlignment = Alignment.BottomEnd
                            ) {

                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(90.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .offset(
                                            x = (-5).dp,
                                            y = (-4).dp
                                        )
                                ) {

                                    IconButton(
                                        onClick = onEditProfile
                                    ) {

                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }

                            Text(
                                text = userName,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Text(
                                text = userEmail,
                                color = MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.7f
                                )
                            )
                        }
                    }
                }

                NavigationDrawerItem(
                    label = {
                        Text("Reportar Incidencia")
                    },
                    selected = false,
                    onClick = {

                        scope.launch {
                            drawerState.close()
                        }

                        onReportIncident()
                    },
                    icon = {

                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null
                        )
                    }
                )

                NavigationDrawerItem(
                    label = {
                        Text("Notificaciones")
                    },
                    selected = false,
                    onClick = {

                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = {

                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null
                        )
                    },
                    badge = {

                        Badge {
                            Text("2")
                        }
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = 8.dp
                    )
                )

                NavigationDrawerItem(
                    label = {
                        Text("Mi perfil")
                    },
                    selected = false,
                    onClick = {

                        scope.launch {
                            drawerState.close()
                        }

                        onEditProfile()
                    },
                    icon = {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    }
                )

                NavigationDrawerItem(
                    label = {
                        Text("Cerrar Sesión")
                    },
                    selected = false,
                    onClick = {

                        scope.launch {
                            drawerState.close()
                        }

                        onLogout()
                    },
                    icon = {

                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    ) {

        Scaffold(
            topBar = {

                TopAppBar(
                    title = {
                        Text("LabManager")
                    },
                    navigationIcon = {

                        IconButton(
                            onClick = {

                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },

            bottomBar = {

                NavigationBar {

                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            onTabSelected(0)
                        },
                        icon = {

                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Equipos")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = {
                            onTabSelected(1)
                        },
                        icon = {

                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Préstamos")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = {
                            onTabSelected(2)
                        },
                        icon = {

                            Icon(
                                imageVector = Icons.Default.Report,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Incidencias")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = {
                            onTabSelected(3)
                        },
                        icon = {

                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Historial")
                        }
                    )
                }
            },

            floatingActionButton = {

                FloatingActionButton(
                    onClick = onScanQr,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {

                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Escanear QR",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.background
                    )
                    .padding(paddingValues)
            ) {

                when (selectedTab) {

                    0 -> {

                        EquipmentContent(
                            equipment = equipment,
                            onEquipmentClick = onEquipmentClick
                        )
                    }

                    1 -> {

                        LoansContent(
                            loans = loans,
                            onLoanClick = onLoanClick
                        )
                    }

                    2 -> {

                        IncidentsContent(
                            incidents = incidents,
                            onReportIncident = onReportIncident,
                            onIncidentClick = onIncidentClick
                        )
                    }

                    3 -> {

                        HistoryContent(
                            historyEntries = historyEntries
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderContent(
    title: String,
    message: String
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(28.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}