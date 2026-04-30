package no.uio.ifi.in2000.ieulrich.team32.data.geocoding

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import no.uio.ifi.in2000.ieulrich.team32.data.client.HttpClientProvider
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.dto.NominatimResponse

class LocationDatasource(private val client: HttpClient = HttpClientProvider.client)  {

    suspend fun getPlaceName(lat: Double, lon: Double): NominatimResponse {
        val response = client.get("https://nominatim.openstreetmap.org/reverse"){
            parameter("lat", lat)
            parameter("lon", lon)
            header("User-Agent", "IN2000 Team 32")
        }
        return response.body<NominatimResponse>()
    }
}