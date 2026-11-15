package com.pe.di

import com.pe.data.remote.api.ApiClient
import com.pe.data.repository.*
import com.pe.domain.repository.*
import com.pe.domain.usecase.Auth.LoginUseCase
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

    factory { LoginUseCase(get()) }
    factory { ObtenerEquiposUseCase(get()) }
    factory { ObtenerPrestamosEstudianteUseCase(get()) }
    factory { ObtenerPrestamosAmbienteUseCase(get()) }
    factory { SolicitarPrestamoUseCase(get()) }
    factory { AprobarSolicitudPrestamoUseCase(get()) }

    viewModel { AuthViewModel(get(), get()) }
    viewModel { EquipoViewModel(get()) }
    viewModel { PrestamoViewModel(get(), get(), get(), get()) }
}
