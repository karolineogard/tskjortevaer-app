package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper

import no.uio.ifi.in2000.ieulrich.team32.ui.util.Format
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.TimeSeries
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails


fun LocationForecastResponse.toForecastByDay(): Map<String, List<ForecastHourDetails>> {
    return properties.timeseries
        .mapNotNull { it.toForeCastHourDetails() }
        .groupBy { Format.extractDate(it.timestamp) }
}
fun TimeSeries.toForeCastHourDetails(): ForecastHourDetails?{
    val nextHours = data.next1Hours ?: data.next6Hours ?: return null
    return ForecastHourDetails(
        symbolCode = nextHours.summary.symbolCode,
        timestamp = time,
        windSpeed = data.instant.details.windSpeed,
        temperature = data.instant.details.airTemperature,
        precipitationAmount = nextHours.details?.precipitationAmount ?: 0.0,
        humidity = data.instant.details.relativeHumidity,
        duration = when {
            data.next1Hours != null -> 1
            else -> 6
        },
        windDirection = data.instant.details.windFromDirection
    )
}