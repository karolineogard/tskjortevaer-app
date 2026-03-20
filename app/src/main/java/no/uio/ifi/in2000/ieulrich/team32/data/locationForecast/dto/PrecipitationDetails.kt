package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PrecipitationDetails(
    @SerialName("precipitation_amount") val precipitationAmount: Double
)