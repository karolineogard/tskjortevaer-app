package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast

import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper.toForecastByDay
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails

class LocationForecastRepository(
    private val dataSource: LocationForecastDataSource = LocationForecastDataSource()
) {
    suspend fun getForecastByDay(lat: Double, lon: Double): Map<String, List<ForecastHourDetails>> {
        return dataSource.getForecast(lat, lon).toForecastByDay()
    }

    suspend fun getForecast(lat: Double, lon: Double): LocationForecastResponse{
        return dataSource.getForecast(lat, lon)
    }
}