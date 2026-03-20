package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val instant: Instant,
    @SerialName("next_1_hours") val next1Hours: NextHours? = null,
    @SerialName("next_6_hours") val next6Hours: NextHours? = null,
    @SerialName("next_12_hours") val next12Hours: NextHours? = null
)