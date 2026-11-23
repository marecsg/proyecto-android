
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
import androidx.compose.material.icons.filled.QrCodeScanner
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
                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {

                    // ENCABEZADO DEL MENÚ
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = PrimarySoft,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Devices,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "LabManager",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextDark
                            )

                            Text(
                                text = "Gestión de laboratorios",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }

                    HorizontalDivider(color = Border)

                    Text(
                        text = "MI CUENTA",
                        modifier = Modifier.padding(
                            start = 28.dp,
                            top = 20.dp,
                            bottom = 8.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    // MI PERFIL
                    NavigationDrawerItem(
                        label = { Text("Mi perfil") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                onEditProfile()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    // AJUSTES
                    NavigationDrawerItem(
                        label = { Text("Ajustes") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                onSettingsClick()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Border
                    )

                    // CERRAR SESIÓN
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Cerrar sesión",
                                color = ErrorText
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                onLogout()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = ErrorText
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    )
                }
            }
        }

    ) {
        Scaffold(
            containerColor = BackgroundClean,


            // BARRA SUPERIOR MODERNIZADA
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Column(
                                verticalArrangement =
                                    Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "LABMANAGER",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Primary
                                )

                                Text(
                                    text = titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextDark
                                )
                            }
                        },

                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            PrimarySoft,
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Abrir menú",
                                        tint = Primary
                                    )
                                }
                            }
                        },

                        actions = {
                            IconButton(
                                onClick = onEditProfile
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            PrimarySoft,
                                            RoundedCornerShape(50.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Mi perfil",
                                        tint = Primary
                                    )
                                }
                            }
                        },

                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = SurfaceWhite
                        )
                    )

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Border
                    )
                }
            },



            // NAVEGACIÓN INFERIOR MODERNIZADA
            bottomBar = {
                Column {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Border
                    )

                    NavigationBar(
                        containerColor = SurfaceWhite,
                        tonalElevation = 0.dp
                    ) {
                        val pestañas = listOf(
                            "Equipos" to Icons.Default.Devices,
                            "Préstamos" to Icons.Default.Receipt,
                            "Incidencias" to Icons.Default.Report,
                            "Historial" to Icons.Default.History
                        )

                        pestañas.forEachIndexed { indice, pestaña ->
                            NavigationBarItem(
                                selected = selectedTab == indice,
                                onClick = {
                                    onTabSelected(indice)
                                },
                                icon = {
                                    Icon(
                                        imageVector = pestaña.second,
                                        contentDescription = pestaña.first,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = pestaña.first,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                alwaysShowLabel = true,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Primary,
                                    selectedTextColor = Primary,
                                    indicatorColor = PrimaryContainer,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )
                        }
                    }
                }
            },



            floatingActionButton = {
                FloatingActionButton(
                    onClick = onScanQr,
                    modifier = Modifier.size(56.dp),
                    containerColor = Primary,
                    contentColor = TextLight,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Escanear código QR",
                        modifier = Modifier.size(26.dp)
                    )
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

