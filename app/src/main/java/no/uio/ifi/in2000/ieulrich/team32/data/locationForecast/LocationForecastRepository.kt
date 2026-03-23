package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast

import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse

class LocationForecastRepository(private val dataSource: LocationForecastDataSource = LocationForecastDataSource()) {
    suspend fun getForecast(lat: Double, lon: Double) : LocationForecastResponse {
        return dataSource.getForecast(lat, lon)
    }
}