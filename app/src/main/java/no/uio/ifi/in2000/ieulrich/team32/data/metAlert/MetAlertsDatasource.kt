package no.uio.ifi.in2000.ieulrich.team32.data.metAlert

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import javax.inject.Inject

class MetAlertsDatasource @Inject constructor(private val client: HttpClient) {
    suspend fun getCurrentAlerts(lat: Double, lon: Double): MetAlertsResponse? {
        Log.d("AlertsRepository", "Get current alerts")
        return try {
            val response = client.get(
                "https://in2000.api.met.no/weatherapi/metalerts/2.0/current.json"
            ) {
                parameter("lat", "%.4f".format(lat))
                parameter("lon", "%.4f".format(lon))
                header("User-Agent", "IN2000 Team 32")
            }
            response.body<MetAlertsResponse>()
        } catch (e: Throwable) {
            Log.e("MetAlerts", "Nettverksfeil: ${e.message}")
            null
        }
    }
}