package no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS

enum class WeatherLayer(val layerName: String, val displayName: String) {
    TEMPERATURE("&layers=air_temperature_2m_ec_sfc_3h_calculations", "Temperatur"),

    PRECIPITATION("&layers=precipitation_amount_3h_ec_sfc_3h_calculations", "Nedbør"),

    WIND("&layers=wind_10m_vector_ec_sfc_3h_calculations", "Vind")
}