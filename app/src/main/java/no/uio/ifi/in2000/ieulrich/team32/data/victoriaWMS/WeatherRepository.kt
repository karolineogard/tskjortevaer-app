package no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlertsResponse
import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer
import javax.inject.Inject

interface WeatherRepository {
    fun getWmsUrl(layer: WeatherLayer, Time: String): String
    fun getAlertsUrl(): String

    suspend fun getAlerts(): List<MetAlert>
}

class WeatherRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : WeatherRepository {
    override fun getWmsUrl(layer: WeatherLayer, Time: String): String {

        return "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetMap" +
                "${layer.layerName}" +
                "&styles=${layer.styleName}" +
                "&crs=EPSG:3857" +
                "&format=image/png" +
                "&transparent=true" +
                "&width=256" +
                "&height=256" +
                "&bbox={bbox-epsg-3857}" +
                "&time=$Time"
    }

    override fun getAlertsUrl(): String {
        return "https://api.met.no/weatherapi/metalerts/2.0/current.json"
    }

    override suspend fun getAlerts(): List<MetAlert> {
        return try {
            val response: MetAlertsResponse = client.get(getAlertsUrl()){
                header(HttpHeaders.UserAgent, "IN2000 Team 32")
            }.body()

            response.features.map{ it.properties }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Kunne ikke hente farevarsler: ${e.message}")
            emptyList()
        }
    }
}
