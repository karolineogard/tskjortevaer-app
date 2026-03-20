package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Units(
    @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevel: String,
    @SerialName("air_pressure_at_sea_level") val airTemperature: String,
    @SerialName("cloud_area_fraction") val cloudAreaFraction: String,
    @SerialName("precipitation_amount") val precipitationAmount: String,
    @SerialName("relative_humidity") val relativeHumidity: String,
    @SerialName("wind_from_direction") val windFromDirection: String,
    @SerialName("wind_speed") val windSpeed: String
)