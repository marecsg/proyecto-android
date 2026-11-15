package com.pe.di

import com.pe.data.remote.api.ApiClient
import com.pe.data.repository.*
import com.pe.domain.repository.*
import com.pe.domain.usecase.Auth.LoginUseCase
import com.pe.domain.usecase.Equipos.ObtenerEquipoPorIdUseCase
import com.pe.domain.usecase.Equipos.ObtenerEquipoPorQrUseCase
import com.pe.domain.usecase.Prestamo.ObtenerPrestamoPorIdUseCase
import com.pe.domain.usecase.Solicitud.CrearSolicitudUseCase
import com.pe.domain.usecase.Solicitud.ObtenerSolicitudPorIdUseCase
import com.pe.domain.usecase.Solicitud.ObtenerSolicitudesEstudianteUseCase
import com.pe.domain.usecase.Solicitud.ObtenerSolicitudesAmbienteUseCase
import com.pe.domain.usecase.Solicitud.CambiarEstadoSolicitudUseCase
import com.pe.domain.usecase.Prestamo.AprobarSolicitudPrestamoUseCase
import com.pe.domain.usecase.Prestamo.CambiarEstadoPrestamoUseCase
import com.pe.domain.usecase.Prestamo.RegistrarDevolucionUseCase
import com.pe.domain.usecase.Equipos.ObtenerEquiposUseCase
import com.pe.domain.usecase.Prestamo.ObtenerPrestamosAmbienteUseCase
import com.pe.domain.usecase.Prestamo.ObtenerPrestamosEstudianteUseCase
import com.pe.presentation.viewmodel.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pe.edu.upeu.data.local.SessionManager

/** Mismo patrón de Koin utilizado en la Booking App del profesor. */
val appModule = module {

    single { createSettings() }
    single { SessionManager(get()) }

    single { ApiClient.httpClient }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<EquipoRepository> { EquipoRepositoryImpl(get(), get()) }
    single<PrestamoRepository> { PrestamoRepositoryImpl(get(), get()) }
    single<SolicitudRepository> {
        SolicitudRepositoryImpl(get(), get())
    }

    factory { LoginUseCase(get()) }
    factory { ObtenerEquiposUseCase(get()) }
    factory { ObtenerPrestamosEstudianteUseCase(get()) }
    factory { ObtenerPrestamosAmbienteUseCase(get()) }
    factory { ObtenerEquipoPorIdUseCase(get()) }
    factory { ObtenerEquipoPorQrUseCase(get()) }

    factory { ObtenerPrestamoPorIdUseCase(get()) }
    factory { CambiarEstadoPrestamoUseCase(get()) }
    factory { RegistrarDevolucionUseCase(get()) }
    factory { AprobarSolicitudPrestamoUseCase(get()) }
    factory { CrearSolicitudUseCase(get()) }
    factory { ObtenerSolicitudPorIdUseCase(get()) }
    factory { ObtenerSolicitudesEstudianteUseCase(get()) }
    factory { ObtenerSolicitudesAmbienteUseCase(get()) }
    factory { CambiarEstadoSolicitudUseCase(get()) }

    viewModel {
        AuthViewModel(get())
    }

    viewModel {
        EquipoViewModel(get(), get(), get())
    }

    viewModel {
        PrestamoViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        SolicitudViewModel(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}
