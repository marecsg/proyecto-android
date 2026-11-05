package com.pe.di

import com.pe.data.remote.api.ApiClient
import com.pe.data.repository.*
import com.pe.domain.repository.*
import com.pe.domain.usecase.*
import com.pe.presentation.viewmodel.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Mismo patrón de Koin utilizado en la Booking App del profesor. */
val appModule = module {
    single { ApiClient.httpClient }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<EquipoRepository> { EquipoRepositoryImpl(get()) }
    single<PrestamoRepository> { PrestamoRepositoryImpl(get()) }

    factory { LoginUseCase(get()) }
    factory { ObtenerEquiposUseCase(get()) }
    factory { ObtenerPrestamosEstudianteUseCase(get()) }
    factory { ObtenerPrestamosEncargadoUseCase(get()) }
    factory { SolicitarPrestamoUseCase(get()) }
    factory { ResponderPrestamoUseCase(get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { EquipoViewModel(get()) }
    viewModel { PrestamoViewModel(get(), get(), get(), get()) }
}
