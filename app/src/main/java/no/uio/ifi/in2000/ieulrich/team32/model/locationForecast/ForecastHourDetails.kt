package no.uio.ifi.in2000.ieulrich.team32.model.locationForecast

data class ForecastHourDetails(
    val symbolCode: String,
    val timestamp: String,
    val windSpeed: String,
    val temperature: String,
    val precipitationAmount: String,
    val duration: Int
)