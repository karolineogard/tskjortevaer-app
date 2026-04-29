package no.uio.ifi.in2000.ieulrich.team32.model.locationForecast

data class ForecastHourDetails(
    val symbolCode: String,
    val timestamp: String,
    val windSpeed: String,
    val temperature: String,
    val precipitationAmount: String,
    val duration: Int,
    val humidity: Double
)

val ForecastHourDetails.imageUrl: String
    get() {
        return "https://raw.githubusercontent.com/metno/weathericons/refs/heads/main/weather/svg/$symbolCode.svg"
    }
