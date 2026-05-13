package no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS

import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.MetAlertsDatasource
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer
import javax.inject.Inject

interface WeatherRepository {
    fun getWmsUrl(layer: WeatherLayer, time: String): String
    fun getAlertsUrl(): String

    suspend fun getAllAlerts(): List<MetAlert>
    suspend fun getAlertsByLocation(lat: Double, lon: Double): List<MetAlert>
}

class WeatherRepositoryImpl @Inject constructor(
    private val datasource: MetAlertsDatasource
) : WeatherRepository {
    override fun getWmsUrl(layer: WeatherLayer, time: String): String {

        return "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetMap" +
                layer.layerName +
                "&styles=${layer.styleName}" +
                "&crs=EPSG:3857" +
                "&format=image/png" +
                "&transparent=true" +
                "&width=256" +
                "&height=256" +
                "&bbox={bbox-epsg-3857}" +
                "&time=$time"
    }

    override fun getAlertsUrl(): String {
        return "https://in2000.api.met.no/weatherapi/metalerts/2.0/current.json"
    }

    override suspend fun getAlertsByLocation(lat: Double, lon: Double): List<MetAlert> =
        datasource.getAlertsByLocation(lat, lon)
            ?.features
            ?.map { it.toMetAlert() }
            ?: emptyList()


    override suspend fun getAllAlerts(): List<MetAlert> =
        datasource.getAllAlerts()
            ?.features
            ?.map { it.toMetAlert() }
            ?: emptyList()
}
