package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import no.uio.ifi.in2000.ieulrich.team32.data.client.HttpClientProvider
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse

class LocationForecastDataSource(private val client: HttpClient = HttpClientProvider.client) {
    private var cachedResponse: LocationForecastResponse? = null
    private var lastUpdatedAt: String? = null

    suspend fun getForecast(lat: Double, lon: Double): LocationForecastResponse? {
        Log.d("LocationForecast", "Api kall for $lat, $lon")
        return try {
            val response = client.get(
                "https://in2000.api.met.no/weatherapi/locationforecast/2.0/compact"
            ) {
                parameter("lat", lat)
                parameter("lon", lon)
                header("User-Agent", "IN2000 Team 32")
                lastUpdatedAt?.let { header("If-Modified-Since", it) }
            }
            when (response.status) {
                HttpStatusCode.NotModified -> {
                    Log.d("LocationForecast", "Ingen endringer, bruker cache")
                    cachedResponse
                }
                else -> {
                    val body = response.body<LocationForecastResponse>()
                    lastUpdatedAt = body.properties.meta.updatedAt
                    cachedResponse = body
                    body
                }
            }
        } catch (e: Throwable) {
            Log.e("LocationForecast", "Nettverksfeil: ${e.message}")
            cachedResponse
        }
    }
}