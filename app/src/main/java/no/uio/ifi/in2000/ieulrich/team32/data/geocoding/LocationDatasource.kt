package no.uio.ifi.in2000.ieulrich.team32.data.geocoding

import android.location.Location
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.dto.NominatimResponse
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.dto.NominatimSearchResult
import java.util.Locale
import javax.inject.Inject

class LocationDatasource @Inject constructor(private val client: HttpClient)  {

    suspend fun getPlaceName(lat: Double, lon: Double): NominatimResponse? {
        return try {
            val response = client.get("https://nominatim.openstreetmap.org/reverse") {
                parameter("lat", "%.4f".format(Locale.US, lat))
                parameter("lon", "%.4f".format(Locale.US, lon))
                parameter("format", "json")
                header("Accept-Language", "no,en")
                header("User-Agent", "IN2000 Team 32")
            }
            response.body<NominatimResponse>()
        } catch (e: Throwable) {
            Log.e("LocationDatasource", "Nettverksfeil ved getPlaceName", e)
            null
        }
    }

    suspend fun searchPlaces(query: String): List<Pair<String, Location>>{
        val response = client.get("https://nominatim.openstreetmap.org/search") {
            parameter("q", query)
            parameter("format", "json")
            parameter("limit", 5)
            parameter("featuretype", "city")
            header("Accept-Language", "no,en")
            header("User-Agent", "IN2000 Team 32")
        }
        return response.body<List<NominatimSearchResult>>().map { result ->
            val location = Location("nominatim").apply {
                latitude = result.lat.toDouble()
                longitude = result.lon.toDouble()
            }
            Pair(result.displayName.split(",").take(2).joinToString(", "), location)
        }
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
            Log.e("LocationDatasource", "error getting coordinates", e)
            null
        }
    }
}