package no.uio.ifi.in2000.ieulrich.team32.data.metAlert

interface AlertsRepository {
    suspend fun getCurrentAlerts(lat: Double, lon: Double): List<AlertFeature>
}

class NetworkAlertsRepository(
) : AlertsRepository {
    private val datasource: MetAlertsDatasource = MetAlertsDatasource()

    override suspend fun getCurrentAlerts(lat: Double, lon: Double): List<AlertFeature> {
        val response = datasource.getCurrentAlerts(lat, lon)
        val alerts = response.features
        return alerts
    }
}