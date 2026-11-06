package com.pe.di

import com.russhwolf.settings.Settings

actual fun createSettings(): Settings {
   return com.russhwolf.settings.Settings()
}