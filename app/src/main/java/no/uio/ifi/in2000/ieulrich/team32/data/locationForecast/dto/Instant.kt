package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.Serializable

@Serializable
data class Instant(
    val details: InstantDetails
)