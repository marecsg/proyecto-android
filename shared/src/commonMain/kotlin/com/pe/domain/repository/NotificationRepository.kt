package com.pe.domain.repository

interface NotificationRepository {
    fun isEnabled(): Boolean
    fun setEnabled(enabled: Boolean)
    suspend fun sendTestNotification(): Result<Unit>
}