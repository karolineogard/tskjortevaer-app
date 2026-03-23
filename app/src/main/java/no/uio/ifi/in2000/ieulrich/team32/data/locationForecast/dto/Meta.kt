package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("updated_at") val updatedAt: String,
    val units: Units
)