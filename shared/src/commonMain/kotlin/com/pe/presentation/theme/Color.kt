package com.pe.presentation.theme

import androidx.compose.ui.graphics.Color

// --- Primario (azul) ---
val Primary          = Color(0xFF2563EB) // Botones primarios, tab/chip activo, enlaces, borde de foco
val PrimaryDark      = Color(0xFF1D4ED8) // Final del degradado (FAB, logo) y estado pressed
val PrimaryLight     = Color(0xFF3B82F6) // Inicio del degradado (FAB, logo)
val PrimaryContainer = Color(0xFFDBEAFE) // Pill activo del nav, avatares, chip "En atención"
val PrimarySoft      = Color(0xFFEFF6FF) // Fondo de íconos/tiles neutros

// --- Fondos y superficies ---
val BackgroundClean  = Color(0xFFF8FAFC) // Fondo general de pantallas
val SurfaceWhite     = Color(0xFFFFFFFF) // Tarjetas, campos, bottom sheet, barra de navegación
val SurfaceVariant   = Color(0xFFEEF2F7) // Fondo de las pestañas segmentadas
val Border           = Color(0xFFE2E8F0) // Borde de tarjetas y divisores
val BorderStrong     = Color(0xFFCBD5E1) // Borde de campos de texto y chips sin seleccionar
val Divider          = Color(0xFFF1F5F9) // Separadores dentro de listas

// --- Textos ---
val TextDark         = Color(0xFF0F172A) // Títulos y valores
val TextBody         = Color(0xFF334155) // Texto de detalle (fechas, motivos)
val TextMuted        = Color(0xFF64748B) // Subtítulos, códigos, etiquetas
val TextHint         = Color(0xFF94A3B8) // Placeholder y elementos deshabilitados
val TextLight        = Color(0xFFFFFFFF) // Texto sobre botones azules

// --- Estados (siempre en par: texto/ícono + contenedor) ---
val SuccessText      = Color(0xFF15803D) // Disponible, Aprobada, Resuelta, Devuelto
val SuccessContainer = Color(0xFFDCFCE7)

val WarningText      = Color(0xFFB45309) // Prestado, Pendiente, Abierta, Vence hoy, Incidencia
val WarningAccent    = Color(0xFFD97706) // Barras de progreso de aviso
val WarningContainer = Color(0xFFFEF3C7)
val WarningBorder    = Color(0xFFFCD34D) // Borde del botón "Reportar incidencia"

val ErrorText        = Color(0xFFB91C1C) // Mantenimiento, Rechazada, Cancelar
val ErrorAccent      = Color(0xFFDC2626)
val ErrorContainer   = Color(0xFFFEE2E2)
val ErrorBorder      = Color(0xFFFECACA) // Borde del botón "Rechazar"

// --- Escáner (pantalla oscura) ---
val ScannerTop       = Color(0xFF0B1220)
val ScannerBottom    = Color(0xFF020617)
val ScanLine         = Color(0xFF60A5FA)