package no.uio.ifi.in2000.ieulrich.team32.data.geocoding

import android.location.Location
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import no.uio.ifi.in2000.ieulrich.team32.data.client.HttpClientProvider
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.dto.NominatimResponse
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.dto.NominatimSearchResult
import java.nio.DoubleBuffer

class LocationDatasource(private val client: HttpClient = HttpClientProvider.client)  {

    suspend fun getPlaceName(lat: Double, lon: Double): NominatimResponse {
        val response = client.get("https://nominatim.openstreetmap.org/reverse"){
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("format", "json")
            header("User-Agent", "IN2000 Team 32")
        }
        return response.body<NominatimResponse>()
    }

    suspend fun getCoordinatesFromName(name: String): Location?{
        if (name.isBlank()) return null
        return try {
            val response = client.get("https://nominatim.openstreetmap.org/search"){
                parameter("q", name)
                parameter("format", "json")
                parameter("limit", 1)
                header("User-Agent", "IN2000 Team 32")
            }
            val results = response.body<List<NominatimSearchResult>>()
            results.firstOrNull()?.let{
                Location("nominatim").apply {
                    latitude = it.lat.toDouble()
                    longitude = it.lon.toDouble()
                }
            }
        } catch (e: Exception) {
            Log.e("LocationDatasource", "error getting coordinates")
            null
        }
    }
}