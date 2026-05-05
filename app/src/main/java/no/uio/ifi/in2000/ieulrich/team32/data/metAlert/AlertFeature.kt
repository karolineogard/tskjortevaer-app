package no.uio.ifi.in2000.ieulrich.team32.data.metAlert

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlertFeature(
    @SerialName("when") val timeInterval: TimeInterval,
    val properties: AlertProperties
) {
}