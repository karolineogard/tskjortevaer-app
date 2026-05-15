package no.uio.ifi.in2000.ieulrich.team32.data.weather

import no.uio.ifi.in2000.ieulrich.team32.model.weather.WeatherLayer
import javax.inject.Inject

interface WeatherRepository {
    fun getWmsUrl(layer: WeatherLayer, time: String): String
}

class WeatherRepositoryImpl @Inject constructor() : WeatherRepository {
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
}
