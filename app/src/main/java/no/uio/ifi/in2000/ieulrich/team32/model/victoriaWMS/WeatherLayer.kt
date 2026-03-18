package no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS

enum class WeatherLayer(val layerName: String, val displayName: String) {
    TEMPERATURE("&layers=air_temperature_2m_ec_vdiv_1h_calculations", "Temperatur"),

    PRECIPITATION("&layers=precipitation_amount_1h_ec_vdiv_1h_calculations", "Nedbør"),

    WIND("&layers=wind_10m_vector_ec_vdiv_1h_calculations", "Vind")
}