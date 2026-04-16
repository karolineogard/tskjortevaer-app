package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper

import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.TimeSeries
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails


fun LocationForecastResponse.toForecastByDay(): Map<String, List<ForecastHourDetails>> {
    return properties.timeseries
        .mapNotNull { it.toForeCastHourDetails() }
        .groupBy { it.timestamp.substringBefore("T") }
}
fun TimeSeries.toForeCastHourDetails(): ForecastHourDetails?{
    val nextHours = data.next1Hours ?: data.next6Hours ?: return null
    return ForecastHourDetails(
        symbolCode = nextHours.summary.symbolCode,
        timestamp = time,
        windSpeed = Format.formatWind(data.instant.details.windSpeed.toString()),
        temperature = Format.formatTemp(data.instant.details.airTemperature.toString()),
        precipitationAmount = Format.formatPrecipitation(nextHours.details?.precipitationAmount.toString() ?: "0.0")
    )
}