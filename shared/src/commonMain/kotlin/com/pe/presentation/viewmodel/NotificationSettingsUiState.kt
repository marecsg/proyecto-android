
package com.pe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.domain.usecase.Notificaciones.DesvincularFcmTokenUseCase
import com.pe.domain.usecase.Notificaciones.GetNotificationSettingsUseCase
import com.pe.domain.usecase.Notificaciones.RegistrarFcmTokenUseCase
import com.pe.domain.usecase.Notificaciones.SetNotificationEnabledUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationSettingsUiState(
    val isEnabled: Boolean = false,
    val isUpdating: Boolean = false,
    val feedbackMessage: String? = null,
    val isFeedBackError: Boolean = false
)

class NotificationSettingsViewModel(
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val setNotificationEnabledUseCase: SetNotificationEnabledUseCase,
    private val registrarFcmTokenUseCase: RegistrarFcmTokenUseCase,
    private val desvincularFcmTokenUseCase: DesvincularFcmTokenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NotificationSettingsUiState(
            isEnabled = getNotificationSettingsUseCase().isEnabled
        )
    )

    val uiState: StateFlow<NotificationSettingsUiState> =
        _uiState.asStateFlow()

    fun onPermissionResult(
        granted: Boolean,
        tokenFcm: String?
    ) {
        if (granted) {
            setNotificationEnabled(true, tokenFcm)
        } else {
            _uiState.update {
                it.copy(
                    feedbackMessage = "Permiso de notificaciones denegado",
                    isFeedBackError = true
                )
            }
        }
    }

    fun setNotificationEnabled(
        enabled: Boolean,
        tokenFcm: String?
    ) {
        if (_uiState.value.isUpdating) return

        if (tokenFcm.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    feedbackMessage = "No se pudo obtener el token Firebase",
                    isFeedBackError = true
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdating = true,
                    feedbackMessage = null
                )
            }

            val resultado = if (enabled) {
                registrarFcmTokenUseCase(tokenFcm)
            } else {
                desvincularFcmTokenUseCase(tokenFcm)
            }

            resultado.fold(
                onSuccess = {
                    setNotificationEnabledUseCase(enabled)

                    _uiState.update {
                        it.copy(
                            isEnabled = enabled,
                            isUpdating = false,
                            feedbackMessage = if (enabled) {
                                "Notificaciones activadas"
                            } else {
                                "Notificaciones desactivadas"
                            },
                            isFeedBackError = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            feedbackMessage = error.message
                                ?: "No se pudieron actualizar las notificaciones",
                            isFeedBackError = true
                        )
                    }
                }
            )
        }
    }
}
