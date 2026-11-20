
package com.pe.di

import com.pe.data.remote.api.ApiClient
import com.pe.data.repository.*
import com.pe.domain.repository.*

import com.pe.domain.usecase.Auth.LoginUseCase
import com.pe.domain.usecase.Equipos.ObtenerEquipoPorIdUseCase
import com.pe.domain.usecase.Equipos.ObtenerEquipoPorQrUseCase
import com.pe.domain.usecase.Equipos.ObtenerEquiposUseCase

import com.pe.domain.usecase.Prestamo.*
import com.pe.domain.usecase.Solicitud.*
import com.pe.domain.usecase.Historial.*
import com.pe.domain.usecase.Notificaciones.GetNotificationSettingsUseCase
import com.pe.domain.usecase.Notificaciones.SendTestNotificationUseCase
import com.pe.domain.usecase.Notificaciones.SetNotificationEnabledUseCase

import com.pe.presentation.viewmodel.*

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pe.edu.upeu.data.local.SessionManager

val appModule = module {

    // CONFIGURACIÓN GENERAL
    single { createSettings() }
    single { SessionManager(get()) }

    single { ApiClient.httpClient }

    // REPOSITORIES
    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }

    single<EquipoRepository> {
        EquipoRepositoryImpl(get(), get())
    }

    single<PrestamoRepository> {
        PrestamoRepositoryImpl(get(), get())
    }

    single<SolicitudRepository> {
        SolicitudRepositoryImpl(get(), get())
    }

    // NUEVO: REPOSITORY DE INCIDENCIAS
    single<IncidenciaRepository> {
        IncidenciaRepositoryImpl(get(), get())
    }

    // USE CASES - AUTENTICACIÓN
    factory { LoginUseCase(get()) }

    // USE CASES - EQUIPOS
    factory { ObtenerEquiposUseCase(get()) }
    factory { ObtenerEquipoPorIdUseCase(get()) }
    factory { ObtenerEquipoPorQrUseCase(get()) }

    // USE CASES - PRÉSTAMOS
    factory { ObtenerPrestamosEstudianteUseCase(get()) }
    factory { ObtenerPrestamosAmbienteUseCase(get()) }
    factory { ObtenerPrestamoPorIdUseCase(get()) }
    factory { CambiarEstadoPrestamoUseCase(get()) }
    factory { RegistrarDevolucionUseCase(get()) }
    factory { AprobarSolicitudPrestamoUseCase(get()) }

    // USE CASES - SOLICITUDES
    factory { CrearSolicitudUseCase(get()) }
    factory { ObtenerSolicitudPorIdUseCase(get()) }
    factory { ObtenerSolicitudesEstudianteUseCase(get()) }
    factory { ObtenerSolicitudesAmbienteUseCase(get()) }
    factory { CambiarEstadoSolicitudUseCase(get()) }

    // USE CASES - HISTORIAL
    factory { ObtenerHistorialEstudianteUseCase(get()) }
    factory { ObtenerHistorialEncargadoUseCase(get()) }

    // VIEWMODELS
    viewModel {
        AuthViewModel(get())
    }

    viewModel {
        EquipoViewModel(get(), get(), get())
    }

    viewModel {
        PrestamoViewModel(
            get(), get(), get(),
            get(), get(), get()
        )
    }

    viewModel {
        SolicitudViewModel(
            get(), get(), get(),
            get(), get()
        )
    }

    // NUEVO: VIEWMODEL DE INCIDENCIAS
    viewModel {
        IncidenciaViewModel(get())
    }

    // VIEWMODEL DE HISTORIAL
    viewModel {
        HistorialViewModel(get(), get())
    }

    // REPOSITORY DE HISTORIAL
    single<HistorialRepository> {
        HistorialRepositoryImpl(get(), get())
    }

    //notif
    single<NotificationRepository>{ NotificationRepositoryImpl(get(),get()) }

    factory { GetNotificationSettingsUseCase(get()) }
    factory { SetNotificationEnabledUseCase(get()) }
    factory { SendTestNotificationUseCase(get()) }

    viewModel {
        NotificationSettingsViewModel(get(), get(), get())
    }

}
