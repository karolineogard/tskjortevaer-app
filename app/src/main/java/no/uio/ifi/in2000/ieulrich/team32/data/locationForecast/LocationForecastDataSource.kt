package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse

class LocationForecastDataSource (private val client: HttpClient) {
    suspend fun getForecast(lat: Double = 60.0, lon: Double = 11.0) : LocationForecastResponse {
        return client.get (
            "https://in2000.api.met.no/weatherapi/locationforecast/2.0/compact"
        ){
            parameter("lat", lat)
            parameter("lon", lon)
        }.body()
    }
}