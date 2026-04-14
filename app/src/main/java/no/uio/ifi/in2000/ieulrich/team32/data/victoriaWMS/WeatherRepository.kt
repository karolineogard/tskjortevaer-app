package no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS

import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

interface WeatherRepository {
    fun getWmsUrl(layer: WeatherLayer): String
    fun getAlertsUrl(): String
}

class WeatherRepositoryImpl : WeatherRepository {
    override fun getWmsUrl(layer: WeatherLayer): String {
        val currentTime = Instant.now()
            .atZone(ZoneOffset.UTC)
            .let { zdt ->
                val roundedHour = ((zdt.hour + 1) / 3) * 3  // runder til nærmeste, ikke alltid ned
                zdt.withHour(roundedHour).truncatedTo(ChronoUnit.HOURS)
            }
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))

        return "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetMap" +
                "${layer.layerName}" +
                "&styles=" +
                "&crs=EPSG:3857" +
                "&format=image/png" +
                "&transparent=true" +
                "&width=256" +
                "&height=256" +
                "&bbox={bbox-epsg-3857}" +
                "&time=$currentTime"

    }

    override fun getAlertsUrl(): String {
        return "https://api.met.no/weatherapi/metalerts/2.0/current.json"
    }
}
