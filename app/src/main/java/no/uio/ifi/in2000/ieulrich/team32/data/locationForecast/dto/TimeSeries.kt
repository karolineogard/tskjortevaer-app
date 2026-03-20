package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimeSeries(
    val time: String,
    val data: Data
)