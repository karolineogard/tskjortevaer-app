package no.uio.ifi.in2000.ieulrich.team32.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.AlertsRepository
import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.NetworkAlertsRepository
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepository
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAlertsRepository(
        impl: NetworkAlertsRepository
    ): AlertsRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository
}