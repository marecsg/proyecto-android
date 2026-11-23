package com.pe.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pe.presentation.viewmodel.NotificationSettingsViewModel
import androidx.compose.runtime.getValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsContent(
    viewModel: NotificationSettingsViewModel,
    tokenFcm: String?,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val permissionLauncher = rememberNotificationPermissionLauncher{granted ->
        viewModel.onPermissionResult(
            granted = granted,
            tokenFcm = tokenFcm
        )}

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Ajustes de notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("Notificaciones", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Recibe avisos sobre solicitudes, préstamos e incidencias.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Switch(
                    checked = state.isEnabled,
                    enabled = !state.isUpdating,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            permissionLauncher.request()
                        } else {
                        viewModel.setNotificationEnabled(
                            enabled = false,
                            tokenFcm = tokenFcm
                        )
                        }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            state.feedbackMessage?.let { message ->
                Spacer(Modifier.height(16.dp))
                Text(
                    text = message,
                    color = if (state.isFeedBackError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}