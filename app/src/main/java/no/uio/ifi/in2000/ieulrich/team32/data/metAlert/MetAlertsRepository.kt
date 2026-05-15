package no.uio.ifi.in2000.ieulrich.team32.data.metAlert

import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import javax.inject.Inject

class MetAlertsRepository @Inject constructor(private val datasource: MetAlertsDatasource){
    suspend fun getAlertsByLocation(lat: Double, lon: Double): List<MetAlert> =
        datasource.getAlertsByLocation(lat, lon)
            ?.features
            ?.map { it.toMetAlert() }
            ?: emptyList()

    suspend fun getAllAlerts(): List<MetAlert> =
        datasource.getAllAlerts()
            ?.features
            ?.map { it.toMetAlert() }
            ?: emptyList()

    fun getAlertsUrl(): String {
        return "https://in2000.api.met.no/weatherapi/metalerts/2.0/current.json"
    }
}