package no.uio.ifi.in2000.ieulrich.team32.data.metAlert

import javax.inject.Inject

interface AlertsRepository {
    suspend fun getCurrentAlerts(lat: Double, lon: Double): List<AlertFeature>
}

class NetworkAlertsRepository @Inject constructor(
    private val api: MetAlertsDatasource
) : AlertsRepository {

    override suspend fun getCurrentAlerts(lat: Double, lon: Double): List<AlertFeature> {
        val response = api.getCurrentAlerts(lat, lon) ?: return emptyList()
        return response.features
    }
}