package com.pe.domain.usecase.Notificaciones

import com.pe.domain.repository.NotificationRepository

class SetNotificationEnabledUseCase(
    private val repository: NotificationRepository
) {
    operator fun invoke(enabled: Boolean) = repository.setEnabled(enabled)
}