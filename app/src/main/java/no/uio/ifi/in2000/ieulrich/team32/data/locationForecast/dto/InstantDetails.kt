package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstantDetails(
    @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevel: Double,
    @SerialName("air_temperature") val airTemperature: Double,
    @SerialName("cloud_area_fraction") val cloudAreaFraction: Double,
    @SerialName("relative_humidity") val relativeHumidity: Double,
    @SerialName("wind_from_direction") val windFromDirection: Double,
    @SerialName("wind_speed") val windSpeed: Double
)