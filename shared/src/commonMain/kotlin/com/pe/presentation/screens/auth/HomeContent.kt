
package com.pe.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pe.presentation.theme.*

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onScanQr: () -> Unit,
    contenido: @Composable () -> Unit

) {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    val titulo = when (selectedTab) {
        0 -> "Equipos"
        1 -> "Préstamos"
        2 -> "Incidencias"
        3 -> "Historial"
        else -> "LabManager"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SurfaceWhite
            ) {
                // Encabezado del menú lateral
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "LabManager",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextLight
                    )

                    Text(
                        text = "Gestión de laboratorios",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextLight.copy(alpha = 0.8f)
                    )
                }

                Spacer(Modifier.height(12.dp))

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
                    label = { Text("Ajustes") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close(); onSettingsClick() } },
                    icon = { Icon(Icons.Default.Settings, null) }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = 8.dp
                    )
                )

                NavigationDrawerItem(
                    label = {
                        Text("Cerrar sesión")
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
            containerColor = BackgroundClean,

            // BARRA SUPERIOR
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = titulo,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextDark
                        )
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
                                contentDescription = "Abrir menú",
                                tint = TextDark
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundClean
                    )
                )
            },

            // NAVEGACIÓN INFERIOR
            bottomBar = {
                NavigationBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            onTabSelected(0)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = "Equipos"
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
                                contentDescription = "Préstamos"
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
                                contentDescription = "Incidencias"
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
                                contentDescription = "Historial"
                            )
                        },
                        label = {
                            Text("Historial")
                        }
                    )
                }
            },

            // BOTÓN PARA ESCANEAR QR
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onScanQr,
                    containerColor = Primary,
                    contentColor = TextLight,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null
                        )

                        Text(
                            text = "Escanear",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        ) { paddingValues ->

            // CONTENEDOR DINÁMICO
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundClean)
                    .padding(paddingValues)
            ) {
                contenido()
            }
        }
    }
}

