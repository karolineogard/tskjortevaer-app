package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    // kan også legge til 6 og 12 timer om vi trenger
    val instant: Instant,
    @SerialName("next_1_hours") val next1Hours: NextHours? = null,
)