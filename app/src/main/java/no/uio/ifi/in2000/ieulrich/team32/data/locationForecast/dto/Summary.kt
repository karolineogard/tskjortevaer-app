package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Summary(
    @SerialName("symbol_code") val symbolCode: String
)