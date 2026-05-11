package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import javax.inject.Inject
import kotlin.math.abs

class LocationForecastDataSource @Inject constructor(
    private val client: HttpClient
) {
    private var cachedResponse: LocationForecastResponse? = null
    private var lastModified: String? = null
    private var cachedLat: Double? = null
    private var cachedLon: Double? = null

    suspend fun getForecast(lat: Double, lon: Double): LocationForecastResponse? {
        val locationChanged = !isSameLocation(lat, lon)
        if (locationChanged) {
            lastModified = null
            cachedResponse = null
        }
        Log.d("LocationForecast", "Api kall for $lat, $lon")
        return try {
            val response = client.get(
                "https://in2000.api.met.no/weatherapi/locationforecast/2.0/compact"
            ) {
                parameter("lat", "%.4f".format(lat))
                parameter("lon", "%.4f".format(lon))
                header("User-Agent", "IN2000 Team 32")
                lastModified?.let { header("If-Modified-Since", it) }
            }
            when (response.status) {
                HttpStatusCode.NotModified -> {
                    Log.d("LocationForecast", "Ingen endringer, bruker cache")
                    cachedResponse
                }
                else -> {
                    val body = response.body<LocationForecastResponse>()
                    lastModified = response.headers["Last-Modified"]
                    cachedResponse = body
                    cachedLat = lat
                    cachedLon = lon
                    body
                }
            }
        } catch (e: Throwable) {
            Log.e("LocationForecast", "Nettverksfeil", e)
            cachedResponse
        }
    }

    private fun isSameLocation(lat: Double, lon: Double): Boolean {
        val cachedLat = cachedLat ?: return false
        val cachedLon = cachedLon ?: return false
        // passer på å ikke forkaste cache hvis koordinater bare har endret seg litt
        return abs(lat - cachedLat) < 0.01 && abs(lon - cachedLon) < 0.01
    }
}