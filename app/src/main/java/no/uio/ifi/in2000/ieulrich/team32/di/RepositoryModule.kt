package no.uio.ifi.in2000.ieulrich.team32.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.ieulrich.team32.data.weather.WeatherRepository
import no.uio.ifi.in2000.ieulrich.team32.data.weather.WeatherRepositoryImpl
import javax.inject.Singleton

/* warning sier at denne ikke blir brukt, men det er bare i vårt
 eget program at dette stemmer. den blir brukt i bakgrunnen av hilt */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository
}