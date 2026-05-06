package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast

import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper.toForeCastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper.toForecastByDay
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails

class LocationForecastRepository(
    private val dataSource: LocationForecastDataSource = LocationForecastDataSource()
) {
    var forecastByDay: Map<String, List<ForecastHourDetails>> = emptyMap()

    suspend fun getForecastByDay(lat: Double, lon: Double): Map<String, List<ForecastHourDetails>> {
        val response = dataSource.getForecast(lat, lon) ?: return forecastByDay
        forecastByDay = response.toForecastByDay()
        return forecastByDay
    }

    suspend fun getForecast(lat: Double, lon: Double): LocationForecastResponse? {
        return dataSource.getForecast(lat, lon)
    }

    suspend fun getForecastNow(lat: Double, lon: Double): ForecastHourDetails? {
        return getForecast(lat, lon)?.properties?.timeseries?.firstOrNull()?.toForeCastHourDetails()
    }
}