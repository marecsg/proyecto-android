package com.pe.domain.usecase.Notificaciones

import com.pe.domain.model.NotificationSettings
import com.pe.domain.repository.NotificationRepository

class GetNotificationSettingsUseCase(
    private val repository: NotificationRepository
) {
    operator  fun invoke() = NotificationSettings(
        isEnabled = repository.isEnabled())
}