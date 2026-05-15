package no.uio.ifi.in2000.ieulrich.team32.model.weather

enum class WeatherLayer(val layerName: String, val displayName: String, val styleName: String) {
    TEMPERATURE("&layers=air_temperature_2m_ec_sfc_3h_calculations", "Temperatur", ""),

    PRECIPITATION("&layers=precipitation_amount_3h_ec_sfc_3h_calculations", "Nedbør", "Precipitation_3h"),

    WIND("&layers=wind_100m_speed_ec_sfc_3h_calculations", "Vind", "")
}