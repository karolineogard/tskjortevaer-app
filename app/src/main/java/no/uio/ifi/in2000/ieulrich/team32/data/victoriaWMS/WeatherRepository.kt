package no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS

import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer


interface WeatherRepository{
    fun getWmsUrl(layer: WeatherLayer): String
}

class WeatherRepositoryImpl: WeatherRepository {
    override fun getWmsUrl(layer: WeatherLayer): String {
        return "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetMap" +
                "${layer.layerName}" +
                "&styles=" +
                "&crs=EPSG:3857" +
                "&format=image/png" +
                "&transparent=true" +
                "&width=256" +
                "&height=256" +
                "&bbox={bbox-epsg-3857}" +
                "&time=2026-03-25T12:00:00Z"
    }
}
