package no.uio.ifi.in2000.ieulrich.team32.data.geocoding.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NominatimSearchResult(
    val lat: String,
    val lon: String,
    @SerialName("display_name") val displayName: String = ""
)