package no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS


interface WeatherRepository{
    fun getWmsUrl(): String
}

class WeatherRepositoryImpl: WeatherRepository {
    override fun getWmsUrl(): String {
        return "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetMap" +
                "&layers=air_temperature_2m_ec_vdiv_1h_calculations" +
                "&styles=" +
                "&crs=EPSG:3857" +
                "&format=image/png" +
                "&transparent=true" +
                "&width=256" +
                "&height=256" +
                "&bbox={bbox-epsg-3857}" +
                "&time=2026-03-18T11:00:00Z"
    }
}
