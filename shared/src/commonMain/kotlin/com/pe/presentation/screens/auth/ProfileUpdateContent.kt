package com.pe.presentation.screens.auth

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pe.domain.model.Usuario
import com.pe.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileUpdateContent(
    usuario: Usuario,
    onBack: () -> Unit
) {
    val rolTexto = when (usuario.rol.uppercase()) {
        "ESTUDIANTE" -> "Estudiante"
        "ENCARGADO" -> "Encargado"
        else -> usuario.rol
    }

    Scaffold(
        containerColor = BackgroundClean,
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundClean
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Avatar informativo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    tint = Primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = usuario.nombreCompleto,
                style = MaterialTheme.typography.headlineSmall,
                color = TextDark
            )

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = PrimaryContainer
            ) {
                Text(
                    text = rolTexto,
                    color = PrimaryDark,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    ),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(8.dp))

            // Tarjeta de información institucional
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "Información institucional",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark
                    )

                    HorizontalDivider(color = Border)

                    ProfileInfoRow(
                        titulo = "Nombre completo",
                        valor = usuario.nombreCompleto
                    )

                    ProfileInfoRow(
                        titulo = "Código institucional",
                        valor = usuario.codigoInstitucional
                    )

                    ProfileInfoRow(
                        titulo = "Correo institucional",
                        valor = usuario.correo
                    )

                    if (usuario.esEncargado) {
                        ProfileInfoRow(
                            titulo = "Laboratorio asignado",
                            valor = usuario.nombreAmbiente
                                ?: "No registrado"
                        )
                    }
                }
            }

            Text(
                text = "Información de tu cuenta en LabManager",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    titulo: String,
    valor: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted
        )

        Text(
            text = valor.ifBlank { "No registrado" },
            style = MaterialTheme.typography.bodyLarge,
            color = TextDark
        )
    }
}
