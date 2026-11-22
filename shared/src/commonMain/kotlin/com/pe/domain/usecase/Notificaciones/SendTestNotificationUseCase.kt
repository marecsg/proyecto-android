package com.pe.domain.usecase.Notificaciones

import com.pe.domain.repository.NotificationRepository

class SendTestNotificationUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke()=repository.sendTestNotification()
}