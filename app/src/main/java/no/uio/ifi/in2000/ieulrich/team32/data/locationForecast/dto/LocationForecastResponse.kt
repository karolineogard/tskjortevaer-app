package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.Serializable


@Serializable
data class LocationForecastResponse(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)